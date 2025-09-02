package co.com.bancolombia.model.loanapplication.exceptions;

public class BootcampInvalidDocumentExceptionException extends RuntimeException {
    public BootcampInvalidDocumentExceptionException() {
        super("The document identity does not exist");
    }
}