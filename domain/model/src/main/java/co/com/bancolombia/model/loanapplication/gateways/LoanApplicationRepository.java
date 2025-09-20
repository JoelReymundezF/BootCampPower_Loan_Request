package co.com.bancolombia.model.loanapplication.gateways;

import co.com.bancolombia.model.loanapplication.LoanApplication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface LoanApplicationRepository {

    Mono<LoanApplication> save(LoanApplication loanApplication);

    Flux<LoanApplication> findAll();

    Flux<LoanApplication> findAllBy(int page, int size);

    Mono<Long> count();

    Flux<LoanApplication> findAllByStatus(Integer status, int page, int size);

    Mono<Long> countByStatus(Integer status);

    Mono<LoanApplication> findById(Integer idLoan);
}
