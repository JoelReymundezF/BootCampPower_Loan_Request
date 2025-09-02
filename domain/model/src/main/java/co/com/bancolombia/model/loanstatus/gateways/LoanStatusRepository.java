package co.com.bancolombia.model.loanstatus.gateways;

import co.com.bancolombia.model.loanstatus.LoanStatus;
import reactor.core.publisher.Mono;

import java.math.BigInteger;

public interface LoanStatusRepository {

    Mono<Boolean> existsById(Integer id);
}
