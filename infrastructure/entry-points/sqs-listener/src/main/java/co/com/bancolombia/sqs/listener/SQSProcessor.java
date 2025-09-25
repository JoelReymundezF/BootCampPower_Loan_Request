package co.com.bancolombia.sqs.listener;

import co.com.bancolombia.model.loanapplication.dto.LoanCapacityResponseDTO;
import co.com.bancolombia.model.loanapplication.dto.LoanDebCapacityDTO;
import co.com.bancolombia.model.loanstatus.LoanStatusEnum;
import co.com.bancolombia.usecase.loanapplication.LoanApplicationUseCase;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.function.Function;

@Service
@Slf4j
@RequiredArgsConstructor
public class SQSProcessor implements Function<Message, Mono<Void>> {
    // private final MyUseCase myUseCase;
private final LoanApplicationUseCase loanApplicationUseCase;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public Mono<Void> apply(Message message) {
        System.out.println(message.body());
        log.info("Mensaje recibido: {}", message.body());
        LoanCapacityResponseDTO loan;
        try {
            loan = objectMapper.readValue(message.body(), LoanCapacityResponseDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Integer statusId = LoanStatusEnum.getIdFromName(loan.decision());
        return loanApplicationUseCase
                .updateLoanStatus(loan.idLoan(), statusId)
                .doOnSuccess(v -> log.info("Loan {} actualizado con status {}", loan.idLoan(), statusId))
                .doOnError(e -> log.error("Error actualizando loan {}", loan.idLoan(), e))
                .then();
    }
}
