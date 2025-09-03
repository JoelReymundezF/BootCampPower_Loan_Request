package co.com.bancolombia.model.loanapplication.exceptions;

public class BootcampInvalidDocumentException extends RuntimeException {
    public BootcampInvalidDocumentException() {
        super("The document identity does not exist");
    }
}