package co.com.bancolombia.model.loantype.gateways;

import co.com.bancolombia.model.loanapplication.LoanApplication;
import co.com.bancolombia.model.loantype.LoanType;
import reactor.core.publisher.Mono;

public interface LoanTypeRepository {
    Mono<Boolean> existsById(Integer id);
    Mono<LoanType> findById(Integer id);
}
