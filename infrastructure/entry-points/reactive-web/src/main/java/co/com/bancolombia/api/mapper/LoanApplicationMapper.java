package co.com.bancolombia.api.mapper;


import co.com.bancolombia.api.dto.CreateLoanApplicationDTO;
import co.com.bancolombia.api.dto.LoanApplicationDTO;
import co.com.bancolombia.api.dto.UpdateLoanApplicationDTO;
import co.com.bancolombia.model.loanapplication.LoanApplication;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LoanApplicationMapper {

    LoanApplicationDTO toResponse(LoanApplication loanApplication);

    LoanApplication toModel(CreateLoanApplicationDTO createLoanApplicationDTO);

    LoanApplication toModel(UpdateLoanApplicationDTO updateLoanApplicationDTO);
}
