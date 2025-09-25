package co.com.bancolombia.model.loanapplication.dto;

import java.math.BigDecimal;

public record LoanDTO(
        BigDecimal amount,
        Integer term,
        BigDecimal interestRate
)  {
}
