package co.com.bancolombia.model.loanapplication;

import lombok.Getter;

import java.util.List;

public record PageResponse<T>(List<T> content, long totalElements, int totalPages, int page, int size) {

}