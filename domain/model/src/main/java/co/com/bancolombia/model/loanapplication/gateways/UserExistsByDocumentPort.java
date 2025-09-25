package co.com.bancolombia.model.loanapplication.gateways;

import co.com.bancolombia.model.loanapplication.dto.UserDto;
import reactor.core.publisher.Mono;

public interface  UserExistsByDocumentPort {
    Mono<Boolean> userExistsByDocument(String document);
    Mono<UserDto> getUserExistsByDocument(String document);
}
