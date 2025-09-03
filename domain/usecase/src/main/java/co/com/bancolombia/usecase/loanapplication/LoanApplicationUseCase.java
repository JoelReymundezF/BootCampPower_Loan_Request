package co.com.bancolombia.usecase.loanapplication;

import co.com.bancolombia.model.loanapplication.LoanApplication;
import co.com.bancolombia.model.loanapplication.exceptions.BootcampInvalidDocumentException;
import co.com.bancolombia.model.loanapplication.exceptions.BootcampInvalidLoanTypeException;
import co.com.bancolombia.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.bancolombia.model.loanapplication.gateways.UserExistsByDocumentPort;
import co.com.bancolombia.model.loantype.gateways.LoanTypeRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LoanApplicationUseCase {

    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final UserExistsByDocumentPort userPort;

    public Mono<LoanApplication> saveLoanApplication(LoanApplication loanApplication) {
        Mono<Boolean> userExistsMono = userPort.userExistsByDocument(loanApplication.getIdentityDocument());
        Mono<Boolean> loanTypeExistsMono = loanTypeRepository.existsById(loanApplication.getIdLoanType());

        return Mono.zip(userExistsMono, loanTypeExistsMono)
                .flatMap(tuple -> {
                    Boolean userExists = tuple.getT1();
                    Boolean loanTypeExists = tuple.getT2();

                    if (!userExists) {
                        return Mono.error(new BootcampInvalidDocumentException());
                    }
                    if (!loanTypeExists) {
                        return Mono.error(new BootcampInvalidLoanTypeException());
                    }

                    return loanApplicationRepository.save(loanApplication);
                });
    }
}
