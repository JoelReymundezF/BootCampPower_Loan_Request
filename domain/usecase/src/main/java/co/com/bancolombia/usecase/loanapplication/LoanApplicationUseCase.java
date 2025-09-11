package co.com.bancolombia.usecase.loanapplication;

import co.com.bancolombia.model.loanapplication.LoanApplication;
import co.com.bancolombia.model.loanapplication.PageResponse;
import co.com.bancolombia.model.loanapplication.exceptions.BootcampInvalidDocumentException;
import co.com.bancolombia.model.loanapplication.exceptions.BootcampInvalidLoanTypeException;
import co.com.bancolombia.model.loanapplication.exceptions.BootcampPageSizeValidException;
import co.com.bancolombia.model.loanapplication.exceptions.BootcampUnauthorizedUserException;
import co.com.bancolombia.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.bancolombia.model.loanapplication.gateways.UserExistsByDocumentPort;
import co.com.bancolombia.model.loantype.gateways.LoanTypeRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.annotation.Nullable;

import java.util.List;

@RequiredArgsConstructor
public class LoanApplicationUseCase {

    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final UserExistsByDocumentPort userPort;

    public Mono<LoanApplication> save(LoanApplication loanApplication, String authenticatedUser) {//renombrar a save
        if (!authenticatedUser.equals(loanApplication.getIdentityDocument())) {
            return Mono.error(new BootcampUnauthorizedUserException());
        }
        Mono<Boolean> userExistsMono = userPort.userExistsByDocument(loanApplication.getIdentityDocument());
        Mono<Boolean> loanTypeExistsMono =  loanTypeRepository.existsById(loanApplication.getIdLoanType());

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

    public Mono<PageResponse<LoanApplication>> list(int page, int size, @Nullable Integer status) {
        Mono<List<LoanApplication>> contentMono;
        Mono<Long> countMono;
        if (status != null) {
            contentMono = loanApplicationRepository.findAllByStatus(status, page, size).collectList();
            countMono = loanApplicationRepository.countByStatus(status);
        } else {
            contentMono = loanApplicationRepository.findAllBy(page, size).collectList();
            countMono = loanApplicationRepository.count();
        }
        return contentMono.zipWith(countMono)
                .map(tuple -> {
                    var content = tuple.getT1();
                    var totalElements = tuple.getT2();
                    int totalPages = (int) Math.ceil((double) totalElements / size);
                    return new PageResponse<>(content, totalElements, totalPages, page, size);
                });
    }


}
