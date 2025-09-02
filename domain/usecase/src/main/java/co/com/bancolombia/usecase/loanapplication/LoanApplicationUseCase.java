package co.com.bancolombia.usecase.loanapplication;

import co.com.bancolombia.model.loanapplication.LoanApplication;
import co.com.bancolombia.model.loanapplication.exceptions.BootcampInvalidDocumentExceptionException;
import co.com.bancolombia.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.bancolombia.model.loanapplication.gateways.UserExistsByDocumentPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LoanApplicationUseCase {

    private final LoanApplicationRepository loanApplicationRepository;
    private final UserExistsByDocumentPort userPort;

    public Mono<LoanApplication> saveLoanApplication(LoanApplication loanApplication) {
        return userPort.userExistsByDocument(loanApplication.getIdentityDocument())
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new BootcampInvalidDocumentExceptionException());
                    }
                    return loanApplicationRepository.save(loanApplication);
                });
    }
}
