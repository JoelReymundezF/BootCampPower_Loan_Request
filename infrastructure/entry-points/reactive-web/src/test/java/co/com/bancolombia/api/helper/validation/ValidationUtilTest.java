package co.com.bancolombia.api.helper.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.Set;

import static org.mockito.Mockito.*;

class ValidationUtilTest {

    private Validator validator;
    private ValidationUtil validationUtil;

    @BeforeEach
    void setUp() {
        validator = mock(Validator.class);
        validationUtil = new ValidationUtil(validator);
    }

    static class DummyDTO {
        String name;
    }

    @Test
    void validate_shouldReturnMonoWithDto_whenNoViolations() {
        DummyDTO dto = new DummyDTO();
        when(validator.validate(dto)).thenReturn(Collections.emptySet());

        StepVerifier.create(validationUtil.validate(dto))
                .expectNext(dto)
                .verifyComplete();

        verify(validator, times(1)).validate(dto);
    }

    @Test
    void validate_shouldReturnErrorMono_whenViolationsExist() {
        DummyDTO dto = new DummyDTO();
        ConstraintViolation<DummyDTO> violation = mock(ConstraintViolation.class);
        Set<ConstraintViolation<DummyDTO>> violations = Set.of(violation);
        when(validator.validate(dto)).thenReturn(violations);

        StepVerifier.create(validationUtil.validate(dto))
                .expectErrorMatches(throwable -> throwable instanceof ConstraintViolationException
                        && ((ConstraintViolationException) throwable).getConstraintViolations().size() == 1)
                .verify();

        verify(validator, times(1)).validate(dto);
    }
}
