package co.com.bancolombia.model.loanapplication.exceptions;

public class BootcampRuleException extends RuntimeException {

    private final BootcampRuleCode ruleCode;

    public BootcampRuleException(BootcampRuleCode ruleCode) {
        super(ruleCode.getMessage());
        this.ruleCode = ruleCode;
    }
}
