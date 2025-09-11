package co.com.bancolombia.model.loanapplication.exceptions;

public class BootcampPageSizeValidException extends RuntimeException {
    public BootcampPageSizeValidException() {
        super("Page and size must be valid numbers");
    }
}
