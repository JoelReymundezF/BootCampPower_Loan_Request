package co.com.bancolombia.model.loanapplication.dto;

import java.math.BigDecimal;
import java.util.List;

public record LoanDebCapacityDTO(
        String identityDocument,
        BigDecimal baseSalary,
        Integer idLoan,
        LoanDTO newLoanApplication,
        List<LoanDTO> activatedLoans
        )
{}