package co.com.bancolombia.model.loanapplication.gateways;

import reactor.core.publisher.Mono;

public interface  UserExistsByDocumentPort {
    Mono<Boolean> userExistsByDocument(String document);
}
