package buysell.dao.create;

import buysell.enums.Status;
import java.util.List;
import lombok.Data;

@Data
public class CreateOrderDto {
    private Long userId;  // Идентификатор пользователя
    private List<Long> productIds;  // Список ID продуктов
    private Status status;  // Статус заказа (например, "создан", "оплачен", и т.д.)
}
