package co.com.bancolombia.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoanApplicationDTO {
    private Integer id;
    private BigDecimal amount;
    private Integer term;
    private String identityDocument;
    private String email;
    private Integer idLoanStatus;
    private Integer idLoanType;
}
