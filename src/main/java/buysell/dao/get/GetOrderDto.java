package buysell.dao.get;

import buysell.enums.Status;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetOrderDto {
    private Long id;
    private Long userId;
    private List<Long> productIds;
    private LocalDateTime createdAt;
    private Status status;
}
