package co.com.bancolombia.model.loanapplication.gateways;


public interface LoanEventGateway {
    void publishLoanStatusChanged(String loanId, String newStatus);
}