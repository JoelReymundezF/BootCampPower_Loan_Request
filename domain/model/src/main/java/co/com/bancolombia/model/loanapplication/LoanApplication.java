package co.com.bancolombia.model.loanapplication;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoanApplication {

    private Integer id;
    private BigDecimal amount;
    private Integer term;
    private String identityDocument;
    private String email;
    private Integer idLoanStatus;
    private Integer idLoanType;
    private Boolean automaticValidation;
    private Integer termMonths;
}
