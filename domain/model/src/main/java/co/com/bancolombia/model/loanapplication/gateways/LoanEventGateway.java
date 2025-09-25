package co.com.bancolombia.model.loanapplication.gateways;


import co.com.bancolombia.model.loanapplication.dto.LoanDebCapacityDTO;
import co.com.bancolombia.model.loanapplication.dto.PaymentInstallmentDTO;

import java.util.List;

public interface LoanEventGateway {
    void publishLoanStatusChanged(String loanId, String newStatus, List<PaymentInstallmentDTO> planPago);
    void publishCalculateDebCapacity(LoanDebCapacityDTO loan);
}