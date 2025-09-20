package co.com.bancolombia.model.loanstatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoanStatus {
    private Integer id;
    private String name;
    private String description;

}
