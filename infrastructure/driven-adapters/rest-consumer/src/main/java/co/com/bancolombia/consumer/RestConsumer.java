package co.com.bancolombia.consumer;

import co.com.bancolombia.model.loanapplication.gateways.UserExistsByDocumentPort;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RestConsumer implements UserExistsByDocumentPort {
    private final WebClient client;


    @CircuitBreaker(name = "userExistsByDocument")
    public Mono<Boolean> userExistsByDocument(String document) {
        return ReactiveSecurityContextHolder.getContext()
                .flatMap(ctx -> {
                    String token = ctx.getAuthentication().getCredentials().toString();
                    return client
                            .get()
                            .uri("/api/v1/users/existsByDocument/{document}", document)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .retrieve()
                            .bodyToMono(new ParameterizedTypeReference<ObjectResponse<Boolean>>() {
                            })
                            .map(ObjectResponse::getData);
                });
    }

}
