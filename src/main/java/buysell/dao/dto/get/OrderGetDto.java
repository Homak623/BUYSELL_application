package buysell.dao.dto.get;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderGetDto {
    private Long id;
    private Long userId;
    private List<Long> productIds;
    private LocalDateTime createdAt;
    private String status;
}
