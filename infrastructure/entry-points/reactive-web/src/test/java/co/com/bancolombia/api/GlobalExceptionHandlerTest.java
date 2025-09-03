package co.com.bancolombia.api;

import co.com.bancolombia.model.loanapplication.exceptions.BootcampInvalidDocumentException;
import co.com.bancolombia.model.loanapplication.exceptions.BootcampInvalidLoanTypeException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Set;

import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private ServerWebExchange exchange;
    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        exchange = mock(ServerWebExchange.class, RETURNS_DEEP_STUBS);
        when(exchange.getResponse().setComplete()).thenReturn(Mono.empty());
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleInvalidLoanTypeException() {
        BootcampInvalidLoanTypeException ex = new BootcampInvalidLoanTypeException();
        handler.handle(exchange, ex).block();
        verify(exchange.getResponse()).setStatusCode(HttpStatus.BAD_REQUEST);
    }

    @Test
    void handleInvalidDocumentException() {
        BootcampInvalidDocumentException ex = new BootcampInvalidDocumentException();
        handler.handle(exchange, ex).block();
        verify(exchange.getResponse()).setStatusCode(HttpStatus.BAD_REQUEST);
    }

    @Test
    void handleDefaultException() {
        RuntimeException ex = new RuntimeException("something went wrong");
        handler.handle(exchange, ex).block();
        verify(exchange.getResponse()).setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void handleWhenResponseCommitted() {
        when(exchange.getResponse().isCommitted()).thenReturn(true);
        RuntimeException ex = new RuntimeException("Test exception");
        Mono<Void> result = handler.handle(exchange, ex);
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                                throwable.getMessage().equals("Test exception")
                )
                .verify();
    }

    @Test
    void shouldHandleConstraintViolationException() {
        ServerWebExchange exchange = mock(ServerWebExchange.class);
        MockServerHttpResponse response = new MockServerHttpResponse();
        when(exchange.getResponse()).thenReturn(response);

        ConstraintViolation<Object> violation = mock(ConstraintViolation.class);
        when(violation.getMessage()).thenReturn("Field is required");

        ConstraintViolationException exception = new ConstraintViolationException(Set.of(violation));

        Mono<Void> result = exceptionHandler.handle(exchange, exception);

        StepVerifier.create(result)
                .verifyComplete();

        assert response.getStatusCode() == HttpStatus.BAD_REQUEST;

        String body = response.getBodyAsString().block();
        assert body.contains("\"code\":\"422_001\"");
        assert body.contains("\"errors\":[\"Field is required\"]");
    }
}
