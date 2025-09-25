package co.com.bancolombia.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CreateLoanApplicationDTO {
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    private BigDecimal amount;

    @NotNull(message = "Term is required")
    @Positive(message = "Term must be greater than zero")
    private Integer term;

    @NotBlank(message = "IdentityDocument is required")
    private String identityDocument;

    @NotNull(message = "Email is required")
    private String email;

    private Integer idLoanStatus;

    @NotNull(message = "LoanType is required")
    private Integer idLoanType;

    @NotNull(message = "Automatic validation is required")
    private Boolean automaticValidation;
}
