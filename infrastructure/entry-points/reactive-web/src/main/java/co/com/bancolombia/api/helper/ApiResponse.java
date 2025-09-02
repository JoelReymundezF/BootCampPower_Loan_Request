package co.com.bancolombia.api.helper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
public class ApiResponse<T> {
    private String code;
    private T data;

}