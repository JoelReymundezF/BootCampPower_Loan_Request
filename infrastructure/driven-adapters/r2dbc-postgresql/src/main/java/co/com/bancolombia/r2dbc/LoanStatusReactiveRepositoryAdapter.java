package co.com.bancolombia.r2dbc;

import co.com.bancolombia.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.bancolombia.model.loanstatus.LoanStatus;
import co.com.bancolombia.model.loanstatus.gateways.LoanStatusRepository;
import co.com.bancolombia.r2dbc.entity.LoanStatusEntity;
import co.com.bancolombia.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.math.BigInteger;

@Repository
public class LoanStatusReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        LoanStatus,
        LoanStatusEntity,
        Integer,
        LoanStatusReactiveRepository
        >  implements LoanStatusRepository {
    public LoanStatusReactiveRepositoryAdapter(LoanStatusReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, LoanStatus.class));
    }

    @Override
    public Mono<Boolean> existsById(Integer id) {
        return this.repository.existsById(id);
    }
}
