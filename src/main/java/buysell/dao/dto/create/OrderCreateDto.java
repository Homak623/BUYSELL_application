package buysell.dao.dto.create;

import java.util.List;
import lombok.Data;

@Data
public class OrderCreateDto {
    private Long userId;
    private List<Long> productIds;
    private String status;
}
