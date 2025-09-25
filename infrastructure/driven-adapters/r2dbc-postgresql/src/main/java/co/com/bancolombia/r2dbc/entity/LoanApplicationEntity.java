package co.com.bancolombia.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table("loan_application")
public class LoanApplicationEntity {

    @Id
    @Column("id")
    private Integer id;

    @Column("amount")
    private BigDecimal amount;

    @Column("term")
    private Integer term;

    @Column("identity_document")
    private String identityDocument;

    @Column("email")
    private String email;

    @Column("id_loan_status")
    private Integer idLoanStatus;

    @Column("id_loan_type")
    private Integer idLoanType;

    @Column("automatic_validation")
    private Boolean automaticValidation = Boolean.FALSE;

    @Column("term_months")
    private Integer termMonths;
}


