package buysell.dao.create;

import buysell.enums.Status;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderDto {
    @NotNull(message = "User ID cannot be null")
    @Min(value = 1, message = "User ID must be positive")
    private Long userId;

    @NotEmpty(message = "Product list cannot be empty")
    private List<@Min(1) Long> productIds;

    @NotNull(message = "Status cannot be null")
    private Status status;
}
