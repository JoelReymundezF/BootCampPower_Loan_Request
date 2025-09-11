package co.com.bancolombia.usecase.loanapplication;

import co.com.bancolombia.model.loanapplication.LoanApplication;
import co.com.bancolombia.model.loanapplication.exceptions.BootcampInvalidDocumentException;
import co.com.bancolombia.model.loanapplication.exceptions.BootcampInvalidLoanTypeException;
import co.com.bancolombia.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.bancolombia.model.loanapplication.gateways.UserExistsByDocumentPort;
import co.com.bancolombia.model.loantype.gateways.LoanTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanApplicationUseCaseTest {

    @Mock
    private LoanApplicationRepository loanApplicationRepository;

    @Mock
    private LoanTypeRepository loanTypeRepository;

    @Mock
    private UserExistsByDocumentPort userPort;

    @InjectMocks
    private LoanApplicationUseCase useCase;

    private LoanApplication loanApplication;

    @BeforeEach
    void setUp() {
        loanApplication = LoanApplication.builder()
                .id(1)
                .identityDocument("123456")
                .idLoanType(10)
                .build();
    }

    @Test
    void shouldSaveLoanApplicationWhenUserAndLoanTypeExist() {
        when(userPort.userExistsByDocument("123456")).thenReturn(Mono.just(true));
        when(loanTypeRepository.existsById(10)).thenReturn(Mono.just(true));
        when(loanApplicationRepository.save(any())).thenReturn(Mono.just(loanApplication));
        /// validar que el docuemnto exista , el id
        StepVerifier.create(useCase.save(loanApplication, "123456"))
                .expectNext(loanApplication)
                .verifyComplete();
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExist() {
        when(userPort.userExistsByDocument("123456")).thenReturn(Mono.just(false));
        when(loanTypeRepository.existsById(10)).thenReturn(Mono.just(true));

        StepVerifier.create(useCase.save(loanApplication, "123456"))
                .expectError(BootcampInvalidDocumentException.class)
                .verify();
    }

    @Test
    void shouldThrowExceptionWhenLoanTypeDoesNotExist() {
        when(userPort.userExistsByDocument("123456")).thenReturn(Mono.just(true));
        when(loanTypeRepository.existsById(10)).thenReturn(Mono.just(false));

        StepVerifier.create(useCase.save(loanApplication, "123456"))
                .expectError(BootcampInvalidLoanTypeException.class)
                .verify();
    }
}