package co.com.bancolombia.model.loantype;
import lombok.*;
//import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoanType {
    private Integer id;

    private String name;

    private BigDecimal minAmount;

    private BigDecimal maxAmount;

    private BigDecimal interestRate;

    private Boolean automaticValidation;
}
