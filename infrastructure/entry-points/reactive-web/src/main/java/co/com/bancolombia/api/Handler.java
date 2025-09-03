package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.CreateLoanApplicationDTO;
import co.com.bancolombia.api.dto.LoanApplicationDTO;
import co.com.bancolombia.api.helper.ApiResponse;
import co.com.bancolombia.api.helper.validation.ValidationUtil;
import co.com.bancolombia.api.mapper.LoanApplicationMapper;
import co.com.bancolombia.usecase.loanapplication.LoanApplicationUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class Handler {

    private  final LoanApplicationUseCase loanApplicationUseCase;
    private  final LoanApplicationMapper loanApplicationMapper;
    private final ValidationUtil validationUtil;

    @Operation(
            operationId = "listenSaveLoanApplication",
            summary = "Register new Loan request",
            description = "Receive a CreateLoanApplicationDTO object and store a user in the system.",
            requestBody = @RequestBody(
                    required = true,
                    description = "Loan request data to be registered",
                    content = @Content(schema = @Schema(implementation = CreateLoanApplicationDTO.class))
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Loan request created successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal error",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class)))
            }
    )
    public Mono<ServerResponse> listenSaveLoanApplication(ServerRequest request) {
        return request.bodyToMono(CreateLoanApplicationDTO.class)
                .doOnNext(dto -> log.info("Received request to save Loan request"))
                .flatMap(validationUtil::validate)
                .map(loanApplicationMapper::toModel)
                .flatMap(loanApplicationUseCase::saveLoanApplication)
                .map(loanApplicationMapper::toResponse)
                .flatMap(savedLoanApplicationDto -> {
                    ApiResponse<LoanApplicationDTO> response = new ApiResponse<>(
                            GlobalExceptionHandler.CREATED,
                            savedLoanApplicationDto
                    );
                    return ServerResponse
                            .status(HttpStatus.CREATED)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(response);
                })
                .doOnNext(dto -> log.info("Loan request created successfully"))
                .doOnError(error -> log.error("Error while creating LoanApplication: {}", error.getMessage(), error));
    }

}
