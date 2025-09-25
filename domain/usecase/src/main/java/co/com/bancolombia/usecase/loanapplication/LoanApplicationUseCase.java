package co.com.bancolombia.usecase.loanapplication;

import co.com.bancolombia.model.loanapplication.LoanApplication;
import co.com.bancolombia.model.loanapplication.PageResponse;
import co.com.bancolombia.model.loanapplication.dto.LoanDTO;
import co.com.bancolombia.model.loanapplication.dto.LoanDebCapacityDTO;
import co.com.bancolombia.model.loanapplication.dto.PaymentInstallmentDTO;
import co.com.bancolombia.model.loanapplication.dto.UserDto;
import co.com.bancolombia.model.loanapplication.exceptions.*;
import co.com.bancolombia.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.bancolombia.model.loanapplication.gateways.LoanEventGateway;
import co.com.bancolombia.model.loanapplication.gateways.UserExistsByDocumentPort;
import co.com.bancolombia.model.loanstatus.LoanStatusEnum;
import co.com.bancolombia.model.loanstatus.gateways.LoanStatusRepository;
import co.com.bancolombia.model.loantype.gateways.LoanTypeRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.util.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@RequiredArgsConstructor
public class LoanApplicationUseCase {
    private static final Logger log = LoggerFactory.getLogger(LoanApplicationUseCase.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final LoanStatusRepository loanStatusRepository;
    private final UserExistsByDocumentPort userPort;
    private final LoanEventGateway loanEventGateway;

    public Mono<LoanApplication> save(LoanApplication loanApplication, String authenticatedUser) {//renombrar a save
        if (!authenticatedUser.equals(loanApplication.getIdentityDocument())) {
            return Mono.error(new BootcampUnauthorizedUserException());
        }
        Mono<UserDto> userExistsMono = userPort.getUserExistsByDocument(loanApplication.getIdentityDocument());
        Mono<Boolean> loanTypeExistsMono = loanTypeRepository.existsById(loanApplication.getIdLoanType());

        return Mono.zip(userExistsMono, loanTypeExistsMono)
                .flatMap(tuple -> {
                    UserDto user = tuple.getT1();
                    Boolean loanTypeExists = tuple.getT2();

                    if (!loanTypeExists) {
                        return Mono.error(new BootcampInvalidLoanTypeException());
                    }

                    return loanApplicationRepository.save(loanApplication)
                            .flatMap(loanSaved ->
                                    loanApplicationRepository
                                            .findAllByIdentityDocumentAndStatus(
                                                    loanApplication.getIdentityDocument(),
                                                    LoanStatusEnum.APPROVED.getId()
                                            )
                                            .flatMap(app ->
                                                    loanTypeRepository.findById(app.getIdLoanType())  // obtenemos el tipo de préstamo
                                                            .map(loanType -> new LoanDTO(
                                                                    app.getAmount(),
                                                                    app.getTerm(),
                                                                    loanType.getInterestRate()  // agregamos la tasa al DTO
                                                            ))
                                            )
                                            .collectList()
                                            .flatMap(approvedLoans -> {
                                                // siempre llega una lista, vacía si no hay aprobados
                                                LoanDebCapacityDTO loanDeb = new LoanDebCapacityDTO(
                                                        user.identityDocument(),
                                                        user.baseSalary(),
                                                        loanSaved.getId(),
                                                        new LoanDTO(
                                                                loanSaved.getAmount(),
                                                                loanSaved.getTerm(),
                                                                null
                                                        ),
                                                        approvedLoans
                                                );
                                                log.info("publishCalculateDebCapacity success");
                                                String json = null;
                                                try {
                                                    json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(loanDeb);
                                                } catch (JsonProcessingException e) {
                                                    throw new RuntimeException(e);
                                                }
                                                log.info("LoanDeb enviado a SQS: {}", json);
                                                loanEventGateway.publishCalculateDebCapacity(loanDeb);

                                                return Mono.just(loanSaved);
                                            })
                            );
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

    public Mono<LoanApplication> updateLoanStatus(Integer idLoan, Integer idLoanStatus) {
        if (!LoanStatusEnum.isApprovableStatus(idLoanStatus)) {
            return Mono.error(new BootcampRuleException(BootcampRuleCode.INVALID_STATUS_UPDATE));
        }

        return loanApplicationRepository.findById(idLoan)
                .switchIfEmpty(Mono.error(new BootcampRuleException(BootcampRuleCode.LOAN_NOT_FOUND)))
                .flatMap(loan -> loanStatusRepository.findById(idLoanStatus)
                        .switchIfEmpty(Mono.error(new BootcampRuleException(BootcampRuleCode.LOAN_STATUS_NOT_FOUND)))
                        .flatMap(loanStatus -> {
                            loan.setIdLoanStatus(idLoanStatus);
                            return loanApplicationRepository.save(loan)
                                    .flatMap(updated -> {
                                        String estado = loanStatus.getName();

                                        Mono<List<PaymentInstallmentDTO>> planPagosMono;
                                        if (loanStatus.getId() == LoanStatusEnum.APPROVED.getId()) {
                                            planPagosMono = loanTypeRepository.findById(updated.getIdLoanType())
                                                    .map(loanType -> generarPlanPagos(updated.getAmount(), updated.getTerm(), loanType.getInterestRate()));
                                        } else {
                                            planPagosMono = Mono.just(Collections.emptyList());
                                        }

                                        return planPagosMono
                                                .flatMap(planPagos -> {
                                                    loanEventGateway.publishLoanStatusChanged(
                                                            updated.getId().toString(),
                                                            estado,
                                                            planPagos
                                                    );
                                                    return Mono.just(updated);
                                                });
                                    });
                        })
                );
    }


    public List<PaymentInstallmentDTO> generarPlanPagos(BigDecimal amount, int termMonths, BigDecimal annualRate) {
        List<PaymentInstallmentDTO> plan = new ArrayList<>();
        BigDecimal monthlyRate = annualRate.divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);
        BigDecimal saldo = amount;

        // Fórmula de cuota fija (sistema francés)
        BigDecimal cuota;
        if (monthlyRate.compareTo(BigDecimal.ZERO) > 0) {
            cuota = amount.multiply(monthlyRate)
                    .divide(BigDecimal.ONE.subtract(
                            BigDecimal.ONE.divide((BigDecimal.ONE.add(monthlyRate)).pow(termMonths), 10, RoundingMode.HALF_UP)
                    ), 2, RoundingMode.HALF_UP);
        } else {
            cuota = amount.divide(BigDecimal.valueOf(termMonths), 2, RoundingMode.HALF_UP);
        }

        for (int i = 1; i <= termMonths; i++) {
            BigDecimal interes = saldo.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);
            BigDecimal capital = cuota.subtract(interes).setScale(2, RoundingMode.HALF_UP);
            saldo = saldo.subtract(capital).setScale(2, RoundingMode.HALF_UP);

            PaymentInstallmentDTO dto = new PaymentInstallmentDTO();
            dto.setNumber(i);
            dto.setCapital(capital);
            dto.setInterest(interes);
            dto.setTotal(cuota);
            dto.setDueDate(LocalDate.now().plusMonths(i));

            plan.add(dto);
        }

        return plan;
    }



}
