package co.com.bancolombia.consumer;

import co.com.bancolombia.model.loanapplication.gateways.UserExistsByDocumentPort;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RestConsumer implements UserExistsByDocumentPort{
    private final WebClient client;


    // these methods are an example that illustrates the implementation of WebClient.
    // You should use the methods that you implement from the Gateway from the domain.
    @CircuitBreaker(name = "userExistsByDocument" /*, fallbackMethod = "testGetOk"*/)
    public Mono<Boolean> userExistsByDocument(String document) {
        return client
                .get()
                .uri("/api/v1/users/existsByDocument/{document}", document)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ObjectResponse<Boolean>>() {})
                .map(ObjectResponse::getData);
    }

// Possible fallback method
//    public Mono<String> testGetOk(Exception ignored) {
//        return client
//                .get() // TODO: change for another endpoint or destination
//                .retrieve()
//                .bodyToMono(String.class);
//    }

    @CircuitBreaker(name = "testPost")
    public Mono<ObjectResponse> testPost() {
        ObjectRequest request = ObjectRequest.builder()
            .val1("exampleval1")
            .val2("exampleval2")
            .build();
        return client
                .post()
                .body(Mono.just(request), ObjectRequest.class)
                .retrieve()
                .bodyToMono(ObjectResponse.class);
    }
}
