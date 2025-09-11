package co.com.bancolombia.model.token.gateways;

import co.com.bancolombia.model.token.Token;
import reactor.core.publisher.Mono;

public interface TokenProviderGateway {

    Mono<Boolean> validateToken(String token);

    Mono<String> getUsernameFromToken(String token);

    Mono<Token> getTokenInfo(String token);

}
