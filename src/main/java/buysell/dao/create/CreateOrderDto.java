package buysell.dao.create;

import buysell.enums.Status;
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
    private Long userId;
    private List<Long> productIds;
    private Status status;
}
