package co.com.bancolombia.api;

import co.com.bancolombia.model.loanapplication.exceptions.BootcampInvalidDocumentExceptionException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.lang.NonNullApi;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import org.yaml.snakeyaml.constructor.DuplicateKeyException;
import reactor.core.publisher.Mono;

import javax.management.relation.RoleNotFoundException;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NonNullApi
@Component
@Order(-2)
public class GlobalExceptionHandler implements WebExceptionHandler {

    public static final String CREATED                = "201_001";
    public static final String ERROR_VALIDATION       = "422_001";
    public static final String CLIENT_ALREADY_EXISTS  = "409_001";
    public static final String INTERNAL_ERROR         = "500_001";

    private static final String KEY_STATUS    = "code";
    private static final String KEY_ERROR     = "error";
    private static final String KEY_ERRORS    = "errors";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> handle(ServerWebExchange exchange,  Throwable ex) {
        if (exchange.getResponse().isCommitted()) {
            return Mono.error(ex);
        }

        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();

        switch (ex) {
            case ConstraintViolationException validationEx -> {
                exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
                List<String> errors = validationEx.getConstraintViolations()
                        .stream()
                        .map(ConstraintViolation::getMessage)
                        .toList();
                buildResponseBody(body, ERROR_VALIDATION, "Validation error", errors);
            }
            case BootcampInvalidDocumentExceptionException bootcampInvalidDocumentExceptionException-> {
                exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
                buildResponseBody(body, ERROR_VALIDATION, ex.getMessage(), null);
            }
            default -> {
                exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                buildResponseBody(body, INTERNAL_ERROR, ex.getMessage(), null);
            }
        }

        return writeResponse(exchange, body);
    }

    private void buildResponseBody(Map<String, Object> body, String code, String message, @Nullable List<String> errors) {
        body.put(KEY_STATUS, code);
        body.put(KEY_ERROR, message);
        if (errors != null && !errors.isEmpty()) {
            body.put(KEY_ERRORS, errors);
        }
    }

    private Mono<Void> writeResponse(ServerWebExchange exchange, Map<String, Object> body) {
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(body);
            return exchange.getResponse()
                    .writeWith(Mono.just(exchange.getResponse()
                            .bufferFactory()
                            .wrap(bytes)));
        } catch (Exception e) {
            String fallback = """
                {"status":"500","error":"Error serializando respuesta","timestamp":"%s"}
                """.formatted(Instant.now());
            return exchange.getResponse()
                    .writeWith(Mono.just(exchange.getResponse()
                            .bufferFactory()
                            .wrap(fallback.getBytes())));
        }
    }
}