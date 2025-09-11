package co.com.bancolombia.api.config;

import co.com.bancolombia.model.token.gateways.TokenProviderGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    private final TokenProviderGateway tokenProvider;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();

        return tokenProvider.validateToken(token)
                .flatMap(valid -> {
                    if (!valid) {
                        return Mono.error(new BadCredentialsException("Invalid token"));
                    }
                    return tokenProvider.getTokenInfo(token)
                            .map(tokenInfo -> {
                                List<GrantedAuthority> authorities = List.of(
                                        new SimpleGrantedAuthority("ROLE_" + tokenInfo.getRole())
                                );

                                // subject = email o documento, lo que pongas en el claim "sub"
                                return new UsernamePasswordAuthenticationToken(
                                        tokenInfo.getSubject(), // principal
                                        token,                   // no guardamos credentials
                                        authorities             // roles
                                );
                            });
                });
    }
}
