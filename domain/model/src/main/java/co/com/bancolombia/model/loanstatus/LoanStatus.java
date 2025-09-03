package co.com.bancolombia.model.loanstatus;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoanStatus {
    private Integer id;
    private String name;
    private String description;

}
