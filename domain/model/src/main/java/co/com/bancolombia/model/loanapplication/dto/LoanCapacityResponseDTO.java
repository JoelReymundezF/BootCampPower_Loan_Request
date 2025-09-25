package co.com.bancolombia.model.loanapplication.dto;

import java.math.BigDecimal;

public record LoanCapacityResponseDTO(
        String identityDocument,
        Integer idLoan,
        BigDecimal capacidadMaxima,
        BigDecimal deudaActual,
        BigDecimal capacidadDisponible,
        BigDecimal cuotaNueva,
        String decision
) {}