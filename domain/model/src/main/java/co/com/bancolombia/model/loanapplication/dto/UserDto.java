package co.com.bancolombia.model.loanapplication.dto;

import java.math.BigDecimal;

public record UserDto(String identityDocument, BigDecimal baseSalary) {}