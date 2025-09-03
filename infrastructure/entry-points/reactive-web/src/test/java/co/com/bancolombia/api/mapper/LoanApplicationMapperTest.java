package co.com.bancolombia.api.mapper;

import co.com.bancolombia.api.dto.CreateLoanApplicationDTO;
import co.com.bancolombia.api.dto.LoanApplicationDTO;
import co.com.bancolombia.model.loanapplication.LoanApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class LoanApplicationMapperTest {

    private LoanApplicationMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(LoanApplicationMapper.class);
    }

    @Test
    void shouldMapCreateDTOToModel() {
        CreateLoanApplicationDTO dto = CreateLoanApplicationDTO.builder()
                .amount(new BigDecimal("1000.50"))
                .term(12)
                .identityDocument("123456")
                .email("test@example.com")
                .idLoanStatus(1)
                .idLoanType(10)
                .build();

        LoanApplication model = mapper.toModel(dto);

        assertThat(model).isNotNull();
        assertThat(model.getAmount()).isEqualByComparingTo(dto.getAmount());
        assertThat(model.getTerm()).isEqualTo(dto.getTerm());
        assertThat(model.getIdentityDocument()).isEqualTo(dto.getIdentityDocument());
        assertThat(model.getEmail()).isEqualTo(dto.getEmail());
        assertThat(model.getIdLoanStatus()).isEqualTo(dto.getIdLoanStatus());
        assertThat(model.getIdLoanType()).isEqualTo(dto.getIdLoanType());
    }

    @Test
    void shouldMapModelToResponseDTO() {
        LoanApplication model = LoanApplication.builder()
                .id(1)
                .amount(new BigDecimal("1000.50"))
                .term(12)
                .identityDocument("123456")
                .email("test@example.com")
                .idLoanStatus(1)
                .idLoanType(10)
                .build();

        LoanApplicationDTO dto = mapper.toResponse(model);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(model.getId());
        assertThat(dto.getAmount()).isEqualByComparingTo(model.getAmount());
        assertThat(dto.getTerm()).isEqualTo(model.getTerm());
        assertThat(dto.getIdentityDocument()).isEqualTo(model.getIdentityDocument());
        assertThat(dto.getEmail()).isEqualTo(model.getEmail());
        assertThat(dto.getIdLoanStatus()).isEqualTo(model.getIdLoanStatus());
        assertThat(dto.getIdLoanType()).isEqualTo(model.getIdLoanType());
    }
}
