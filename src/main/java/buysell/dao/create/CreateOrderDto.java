package buysell.dao.create;

import buysell.enums.Status;
import java.util.List;
import lombok.Data;

@Data
public class CreateOrderDto {
    private Long userId;
    private List<Long> productIds;
    private Status status;
}
