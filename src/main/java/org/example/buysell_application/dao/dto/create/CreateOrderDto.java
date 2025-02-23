package org.example.buysell_application.dao.dto.create;

import java.util.List;
import lombok.Data;

@Data
public class CreateOrderDto {
    private Long userId;  // Идентификатор пользователя
    private List<Long> productIds;  // Список ID продуктов
    private String status;  // Статус заказа (например, "создан", "оплачен", и т.д.)
}
