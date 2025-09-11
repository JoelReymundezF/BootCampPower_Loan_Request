package co.com.bancolombia.model.loanapplication.exceptions;

public class BootcampUnauthorizedUserException extends RuntimeException {
    public BootcampUnauthorizedUserException() {
        super("Only clients can create loan requests and only for themselves.");
    }
}
