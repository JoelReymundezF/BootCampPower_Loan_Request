package co.com.bancolombia.consumer;

import co.com.bancolombia.model.loanapplication.gateways.UserExistsByDocumentPort;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;

class RestConsumerTest {

    private static RestConsumer restConsumer;
    private static MockWebServer mockBackEnd;

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
        WebClient webClient = WebClient.builder()
                .baseUrl(mockBackEnd.url("/").toString())
                .build();
        restConsumer = new RestConsumer(webClient);
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @Test
    @DisplayName("Return true when user exists")
    void shouldReturnTrueWhenUserExists() {
        String body = """
                {
                  "code": "200_001",
                  "data": true
                }
                """;
        mockBackEnd.enqueue(new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(200)
                .setBody(body));

        Mono<Boolean> result = restConsumer.userExistsByDocument("12345")
                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(
                        Mono.just(new SecurityContextImpl(
                                new UsernamePasswordAuthenticationToken("user", "token123")
                        ))
                ));

        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    @DisplayName("Return false when user does not exist")
    void shouldReturnFalseWhenUserDoesNotExist() {
        String body = """
                {
                  "code": "200_001",
                  "data": false
                }
                """;
        mockBackEnd.enqueue(new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(200)
                .setBody(body));

        Mono<Boolean> result = restConsumer.userExistsByDocument("99999")
                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(
                        Mono.just(new SecurityContextImpl(
                                new UsernamePasswordAuthenticationToken("user", "token123")
                        ))
                ));

        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();
    }
}
