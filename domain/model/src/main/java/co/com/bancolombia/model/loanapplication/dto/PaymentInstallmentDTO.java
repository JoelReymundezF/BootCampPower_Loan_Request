package co.com.bancolombia.model.loanapplication.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class PaymentInstallmentDTO {
    private int number; // número de cuota
    private BigDecimal capital;
    private BigDecimal interest;
    private BigDecimal total;
    private LocalDate dueDate;
}