package co.com.bancolombia.consumer;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserResponse {
    private String firstName;

    private String lastName;

    private String password;

    private LocalDate birthDate;

    private String address;

    private String identityDocument;

    private String phone;

    private String email;

    private BigDecimal baseSalary;

    private Long roleId;
}
