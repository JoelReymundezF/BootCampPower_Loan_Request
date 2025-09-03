package co.com.bancolombia.r2dbc;

import co.com.bancolombia.model.loanapplication.LoanApplication;
import co.com.bancolombia.r2dbc.entity.LoanApplicationEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanApplicationReactiveRepositoryAdapterTest {

    @InjectMocks
    LoanApplicationReactiveRepositoryAdapter repositoryAdapter;

    @Mock
    LoanApplicationReactiveRepository repository;

    @Mock
    ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(LoanApplicationReactiveRepository.class);
        mapper = Mockito.mock(ObjectMapper.class);
        repositoryAdapter = new LoanApplicationReactiveRepositoryAdapter(repository, mapper);
    }

    @Test
    void shouldSaveAndFindLoanApplication() {
        LoanApplication loanApplication = LoanApplication.builder()
                .id(1)
                .identityDocument("123456789")
                .build();

        LoanApplicationEntity entity = new LoanApplicationEntity();
        entity.setId(1);
        entity.setIdentityDocument("123456789");

        when(mapper.map(any(LoanApplication.class), eq(LoanApplicationEntity.class))).thenReturn(entity);
        when(mapper.map(any(LoanApplicationEntity.class), eq(LoanApplication.class))).thenReturn(loanApplication);

        when(repository.save(any(LoanApplicationEntity.class))).thenReturn(Mono.just(entity));
        when(repository.findById(1)).thenReturn(Mono.just(entity));
        Mono<LoanApplication> result = repositoryAdapter.save(loanApplication);

        StepVerifier.create(result)
                .expectNextMatches(saved -> saved.getIdentityDocument().equals("123456789"))
                .verifyComplete();

        verify(repository, times(1)).save(any(LoanApplicationEntity.class));
        verify(repository, times(1)).findById(1);
    }
}
