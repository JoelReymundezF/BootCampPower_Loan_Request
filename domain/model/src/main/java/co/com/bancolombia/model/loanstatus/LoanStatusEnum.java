package co.com.bancolombia.model.loanstatus;

import lombok.Getter;

@Getter
public enum LoanStatusEnum {
    APPROVED(3),
    REJECTED(4);

    private final int id;

    LoanStatusEnum(int id) {
        this.id = id;
    }

    public static boolean isApprovableStatus(int id) {
        return id == APPROVED.id || id == REJECTED.id;
    }
}
