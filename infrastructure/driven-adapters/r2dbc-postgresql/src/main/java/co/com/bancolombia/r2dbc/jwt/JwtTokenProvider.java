package co.com.bancolombia.r2dbc.jwt;

import co.com.bancolombia.model.token.Token;
import co.com.bancolombia.model.token.gateways.TokenProviderGateway;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class JwtTokenProvider implements TokenProviderGateway {

    private final SecretKey secretKey;

    public JwtTokenProvider(
            @Value("${security.jwt.secret}") String secret
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public Mono<Boolean> validateToken(String token) {
        return Mono.fromSupplier(() -> {
            try {
                Jwts.parserBuilder()
                        .setSigningKey(secretKey)
                        .build()
                        .parseClaimsJws(token);;
                return true;
            } catch (JwtException | IllegalArgumentException e){
                log.info("Invalid Jwt Token : {}",e.getMessage());
                log.info("Invalid Jwt Token trace.",e);
                return false;
            }
        });
    }

    @Override
    public Mono<String> getUsernameFromToken(String token) {
        return Mono.fromSupplier(() -> {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        });
    }

    @Override
    public Mono<Token> getTokenInfo(String token) {
        return Mono.fromSupplier(() -> {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return Token.builder()
                    .subject(claims.getSubject())  // email
                    .role(claims.get("role", String.class))
                    .expiration(claims.getExpiration())
                    .build();
        });
    }

}
