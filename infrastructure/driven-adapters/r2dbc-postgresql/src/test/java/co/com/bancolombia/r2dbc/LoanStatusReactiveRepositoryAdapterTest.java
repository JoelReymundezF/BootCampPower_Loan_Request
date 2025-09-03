package co.com.bancolombia.r2dbc;

import co.com.bancolombia.model.loanstatus.LoanStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanStatusReactiveRepositoryAdapterTest {

    @Mock
    private LoanStatusReactiveRepository repository;

    @Mock
    private ObjectMapper mapper;

    @InjectMocks
    private LoanStatusReactiveRepositoryAdapter adapter;

    @Test
    void shouldReturnTrueWhenExistsById() {
        when(repository.existsById(1)).thenReturn(Mono.just(true));

        StepVerifier.create(adapter.existsById(1))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void shouldReturnFalseWhenNotExistsById() {
        when(repository.existsById(2)).thenReturn(Mono.just(false));

        StepVerifier.create(adapter.existsById(2))
                .expectNext(false)
                .verifyComplete();
    }
}