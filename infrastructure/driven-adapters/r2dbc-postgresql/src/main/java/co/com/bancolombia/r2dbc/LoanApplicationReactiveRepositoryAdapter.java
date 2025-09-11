package co.com.bancolombia.r2dbc;

import co.com.bancolombia.model.loanapplication.LoanApplication;
import co.com.bancolombia.model.loanapplication.PageResponse;
import co.com.bancolombia.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.bancolombia.r2dbc.entity.LoanApplicationEntity;
import co.com.bancolombia.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigInteger;

@Repository
public class LoanApplicationReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        LoanApplication,
        LoanApplicationEntity,
        Integer,
        LoanApplicationReactiveRepository
        > implements LoanApplicationRepository {
    public LoanApplicationReactiveRepositoryAdapter(LoanApplicationReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, LoanApplication.class));
    }

    @Override
    public Mono<LoanApplication> save(LoanApplication loanApplication) {
        return super.save(loanApplication)
                .flatMap(saved -> findById(saved.getId()));
    }

    @Override
    public Flux<LoanApplication> findAll() {

        return super.findAll();
    }

    @Override
    public Flux<LoanApplication> findAllBy(int page, int size) {
        int offset = page * size;
        return repository
                .findAllWithOffset(size, offset)
                .map(entity -> mapper.map(entity, LoanApplication.class));
    }

    @Override
    public Mono<Long> count() {
        return repository.count();
    }

    @Override
    public Flux<LoanApplication> findAllByStatus(Integer status, int page, int size) {
        int offset = page * size;
        return repository
                .findAllByStatus(status, size, offset)
                .map(entity -> mapper.map(entity, LoanApplication.class));
    }

    @Override
    public Mono<Long> countByStatus(Integer status) {
        return repository.countByStatus(status);
    }
}
