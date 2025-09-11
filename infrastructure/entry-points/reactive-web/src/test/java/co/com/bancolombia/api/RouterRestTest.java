package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.CreateLoanApplicationDTO;
import co.com.bancolombia.api.dto.LoanApplicationDTO;
import co.com.bancolombia.api.helper.validation.ValidationUtil;
import co.com.bancolombia.api.mapper.LoanApplicationMapper;
import co.com.bancolombia.model.loanapplication.LoanApplication;
import co.com.bancolombia.usecase.loanapplication.LoanApplicationUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {LoanApplicationRouterRest.class, LoanApplicationHandler.class})
@WebFluxTest
class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    LoanApplicationUseCase loanApplicationUseCase;

    @MockitoBean
    private LoanApplicationMapper loanApplicationMapper;

    @MockitoBean
    private ValidationUtil validationUtil;

    CreateLoanApplicationDTO createLoanApplicationDTO = CreateLoanApplicationDTO.builder()
            .amount(new BigDecimal("1500.00"))
            .term(12)
            .identityDocument("123456789")
            .email("cliente@banco.com")
            .idLoanType(1)
            .idLoanStatus(2)
            .build();

    LoanApplicationDTO loanApplicationDTO = LoanApplicationDTO.builder()
            .amount(new BigDecimal("1500.00"))
            .term(12)
            .identityDocument("123456789")
            .email("cliente@banco.com")
            .idLoanType(1)
            .idLoanStatus(2)
            .build();


    @Test
    void testSaveLoanApplication() {
        LoanApplication loanApplication = LoanApplication.builder()
                .id(1)
                .identityDocument("123456789")
                .build();

        //no usar any usar lo que se requiere mapear
        when(validationUtil.validate(any(CreateLoanApplicationDTO.class))).thenReturn(Mono.just(createLoanApplicationDTO));
        when(loanApplicationMapper.toModel(any(CreateLoanApplicationDTO.class))).thenReturn(loanApplication);
        when(loanApplicationUseCase.save(any())).thenReturn(Mono.just(loanApplication));
        when(loanApplicationMapper.toResponse(any())).thenReturn(loanApplicationDTO);

        webTestClient.post()
                .uri("/api/v1/loanApplications")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(createLoanApplicationDTO)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.code").isEqualTo("201_001")
                .jsonPath("$.data.identityDocument").isEqualTo("123456789");
    }

}
