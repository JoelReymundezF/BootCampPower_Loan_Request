package co.com.bancolombia.model.loanapplication.exceptions;

public class BootcampInvalidLoanTypeException extends RuntimeException {
    public BootcampInvalidLoanTypeException() {
        super("The loan type does not exist");
    }
}