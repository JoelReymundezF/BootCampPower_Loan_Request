package co.com.bancolombia.sqs.sender;

import co.com.bancolombia.model.loanapplication.dto.LoanDebCapacityDTO;
import co.com.bancolombia.model.loanapplication.dto.PaymentInstallmentDTO;
import co.com.bancolombia.model.loanapplication.gateways.LoanEventGateway;
import co.com.bancolombia.sqs.sender.config.SQSSenderProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class SQSSender implements LoanEventGateway {
    private final SQSSenderProperties properties;
    private final SqsAsyncClient client;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Mono<String> send(String message) {
        return Mono.fromCallable(() -> buildRequest(message))
                .flatMap(request -> Mono.fromFuture(client.sendMessage(request)))
                .doOnNext(response -> log.debug("Message sent {}", response.messageId()))
                .map(SendMessageResponse::messageId);
    }

    private SendMessageRequest buildRequest(String message) {
        return SendMessageRequest.builder()
                .queueUrl(properties.queueUrl())
                .messageBody(message)
                .build();
    }

    @Override
    public void publishLoanStatusChanged(String loanId, String status, List<PaymentInstallmentDTO> planPagos) {
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append(String.format("Su préstamo con código %s fue %s.", loanId, status));

        if (planPagos != null && !planPagos.isEmpty()) {
            String planPagosStr = planPagos.stream()
                    .map(p -> String.format(
                            "Cuota %d: Capital=%s, Interés=%s, Total=%s, Vence=%s",
                            p.getNumber(),
                            p.getCapital().setScale(2, BigDecimal.ROUND_HALF_UP),
                            p.getInterest().setScale(2, BigDecimal.ROUND_HALF_UP),
                            p.getTotal().setScale(2, BigDecimal.ROUND_HALF_UP),
                            p.getDueDate()
                    ))
                    .collect(Collectors.joining("; "));

            messageBuilder.append(" Plan de pagos: ").append(planPagosStr);
        }
        String message = messageBuilder.toString();
        Mono.fromCallable(() -> buildRequest(message))
                .flatMap(request -> Mono.fromFuture(client.sendMessage(request)))
                .doOnNext(response -> log.info("Message sent to SQS: {}", response.messageId()))
                .subscribe();
    }


    @Override
    public void publishCalculateDebCapacity(LoanDebCapacityDTO loanDeb) {
        try {
            String body = objectMapper.writeValueAsString(loanDeb);
            Mono.fromCallable(() -> buildRequestCalculateDebCapacity(body))
                    .flatMap(request -> Mono.fromFuture(client.sendMessage(request)))
                    .doOnNext(response -> log.info("Message sent to SQS: {}", response.messageId()))
                    .subscribe();
        } catch (Exception e) {
            throw new RuntimeException("Error serializando LoanDebCapacityDTO", e);
        }
    }

    private SendMessageRequest buildRequestCalculateDebCapacity(String message) {
        return SendMessageRequest.builder()
                .queueUrl(properties.queueDebtCapacityUrl())
                .messageBody(message)
                .build();
    }

}
