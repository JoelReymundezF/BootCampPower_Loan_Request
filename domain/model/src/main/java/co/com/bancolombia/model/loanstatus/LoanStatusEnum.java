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

    public static int getIdFromName(String name) {
        if (name == null) return -1;
        try {
            return LoanStatusEnum.valueOf(name.toUpperCase()).getId();
        } catch (IllegalArgumentException e) {
            return -1;
        }
    }
}
