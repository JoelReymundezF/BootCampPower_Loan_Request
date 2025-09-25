package co.com.bancolombia.r2dbc;

import co.com.bancolombia.model.loanapplication.LoanApplication;
import co.com.bancolombia.r2dbc.entity.LoanApplicationEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface LoanApplicationReactiveRepository extends ReactiveCrudRepository<LoanApplicationEntity, Integer>, ReactiveQueryByExampleExecutor<LoanApplicationEntity> {

    @Query("SELECT * FROM loan_application LIMIT :limit OFFSET :offset")
    Flux<LoanApplicationEntity> findAllWithOffset(int limit, int offset);

    @Query("SELECT * FROM loan_application WHERE id_loan_status = :status LIMIT :limit OFFSET :offset")
    Flux<LoanApplicationEntity> findAllByStatus(Integer status, int limit, int offset);

    @Query("SELECT COUNT(*) FROM loan_application WHERE id_loan_status = :status")
    Mono<Long> countByStatus(Integer status);

    @Query("SELECT * FROM loan_application WHERE identity_document = :identityDocument and id_loan_status = :status")
    Flux<LoanApplicationEntity> findAllByIdentityDocumentAndStatus(String identityDocument, Integer status);
}
