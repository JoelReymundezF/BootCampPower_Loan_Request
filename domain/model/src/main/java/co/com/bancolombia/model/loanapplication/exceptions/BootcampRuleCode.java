package co.com.bancolombia.model.loanapplication.exceptions;

public enum BootcampRuleCode {

    LOAN_STATUS_NOT_FOUND("LOAN_STATUS_NOT_FOUND", "LoanStatus not found"),
    LOAN_NOT_FOUND("LOAN_NOT_FOUND", "Loan application not found"),
    INVALID_STATUS_UPDATE("INVALID_STATUS_UPDATE", "You can only update to approved or rejected");

    private final String code;
    private final String message;

    BootcampRuleCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}