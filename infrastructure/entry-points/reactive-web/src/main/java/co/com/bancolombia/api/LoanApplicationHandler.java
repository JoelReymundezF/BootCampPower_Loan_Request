package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.CreateLoanApplicationDTO;
import co.com.bancolombia.api.dto.LoanApplicationDTO;
import co.com.bancolombia.api.dto.UpdateLoanApplicationDTO;
import co.com.bancolombia.api.helper.ApiResponse;
import co.com.bancolombia.api.helper.validation.ValidationUtil;
import co.com.bancolombia.api.mapper.LoanApplicationMapper;
import co.com.bancolombia.model.loanapplication.PageResponse;
import co.com.bancolombia.usecase.loanapplication.LoanApplicationUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoanApplicationHandler {

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
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .flatMap(auth -> {
                    String username = auth.getName();

                    return request.bodyToMono(CreateLoanApplicationDTO.class)
                            .doOnNext(dto -> log.info("Received request to save Loan request"))
                            .flatMap(validationUtil::validate)
                            .map(loanApplicationMapper::toModel)
                            .flatMap(model -> loanApplicationUseCase.save(model, username))
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
                            });
                })
                .doOnNext(dto -> log.info("Loan request created successfully"))
                .doOnError(error -> log.error("Error while creating LoanApplication: {}", error.getMessage(), error));
    }

    @Operation(
            operationId = "listenListLoanApplication",
            summary = "List loan applications",
            description = "Retrieves a paginated list of loan applications from the system using the provided query parameters.",
            parameters = {
                    @Parameter(
                            name = "page",
                            description = "Page number (zero-based index). Default is 0.",
                            required = false,
                            example = "0",
                            in = ParameterIn.QUERY,
                            schema = @Schema(type = "integer", defaultValue = "0", minimum = "0")
                    ),
                    @Parameter(
                            name = "size",
                            description = "Number of items per page. Default is 10.",
                            required = false,
                            example = "10",
                            in = ParameterIn.QUERY,
                            schema = @Schema(type = "integer", defaultValue = "10", minimum = "1")
                    ),
                    @Parameter(
                            name = "status",
                            description = "Filter loan applications by status",
                            required = false,
                            example = "APPROVED",
                            in = ParameterIn.QUERY,
                            schema = @Schema(type = "string")
                    )
            },
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Loan applications retrieved successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PageResponse.class))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "Invalid query parameters (page or size not numeric)",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(
                                            example = "{ \"code\": \"400_001\", \"error\": \"page and size must be valid numbers\" }"
                                    )
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class))
                    )
            }
    )
    public Mono<ServerResponse> listenListLoanApplication(ServerRequest request) {
        int page;
        int size;
        try {
            page = request.queryParam("page").map(Integer::parseInt).orElse(0);
            size = request.queryParam("size").map(Integer::parseInt).orElse(10);
        } catch (NumberFormatException e) {
            return ServerResponse.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(Map.of(
                            "code", "400_001",
                            "error", "page y size deben ser números válidos"
                    ));
        }
        Integer status = request.queryParam("status")
                .filter(s -> !s.isBlank())
                .map(Integer::parseInt)
                .orElse(null);
        return loanApplicationUseCase.list(page, size, status)
                .flatMap(pageResponse -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(pageResponse)
                );
    }

    @Operation(
            operationId = "listenUpdateLoanStatus",
            summary = "Update loan status",
            description = "Receives an UpdateLoanApplicationDTO object and updates the status of a loan application.",
            requestBody = @RequestBody(
                    required = true,
                    description = "Loan application update data",
                    content = @Content(schema = @Schema(implementation = UpdateLoanApplicationDTO.class))
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Loan status updated successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponse.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "Validation error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponse.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500",
                            description = "Internal error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponse.class)
                            )
                    )
            }
    )
    public Mono<ServerResponse> listenUpdateLoanStatus(ServerRequest request) {
        return request.bodyToMono(UpdateLoanApplicationDTO.class)
                .flatMap(validationUtil::validate)
                .flatMap(updateDTO -> loanApplicationUseCase.updateLoanStatus(updateDTO.getIdLoan(), updateDTO.getIdLoanStatus()))
                .map(loanApplicationMapper::toResponse)
                .flatMap(savedLoanApplicationDto -> {
                    ApiResponse<LoanApplicationDTO> response = new ApiResponse<>(
                            GlobalExceptionHandler.OK,
                            savedLoanApplicationDto
                    );
                    return ServerResponse
                            .status(HttpStatus.OK)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(response);
                })
                .doOnNext(dto -> log.info("Update loanType successfully"))
                .doOnError(error -> log.error("Error while Update loanType: {}", error.getMessage(), error));
    }

}
