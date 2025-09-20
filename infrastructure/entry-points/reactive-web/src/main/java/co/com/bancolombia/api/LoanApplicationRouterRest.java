package co.com.bancolombia.api;

import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class LoanApplicationRouterRest {


    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/loanApplications",
                    beanClass = LoanApplicationHandler.class,
                    beanMethod = "listenSaveLoanApplication"
            )
    })
    @Bean
    public RouterFunction<ServerResponse> routerFunction(LoanApplicationHandler handler) {
        return route(POST("/api/v1/loanApplications"), handler::listenSaveLoanApplication)
                .andRoute(GET("/api/v1/loanApplications"), handler::listenListLoanApplication)
                .andRoute(PUT("/api/v1/loanApplications"), handler::listenUpdateLoanType);
    }
}
