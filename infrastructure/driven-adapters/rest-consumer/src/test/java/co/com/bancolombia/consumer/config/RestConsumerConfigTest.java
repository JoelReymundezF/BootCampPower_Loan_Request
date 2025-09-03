package co.com.bancolombia.consumer.config;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class RestConsumerConfigUnitTest {

    @Test
    void shouldCreateWebClientAndMakeRequest() throws Exception {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setBody("{\"status\":\"ok\"}").addHeader("Content-Type", "application/json"));
        server.start();

        String baseUrl = server.url("/").toString();
        RestConsumerConfig config = new RestConsumerConfig(baseUrl, 5000);
        WebClient client = config.getWebClient(WebClient.builder());

        Mono<String> responseMono = client.get()
                .uri("/")
                .retrieve()
                .bodyToMono(String.class);

        StepVerifier.create(responseMono)
                .expectNext("{\"status\":\"ok\"}")
                .verifyComplete();

        server.shutdown();
    }
}