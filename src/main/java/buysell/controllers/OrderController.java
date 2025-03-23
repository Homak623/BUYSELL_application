package buysell.controllers;

import buysell.dao.create.CreateBulkOrderDto;
import buysell.dao.create.CreateOrderDto;
import buysell.dao.get.GetOrderDto;
import buysell.enums.Status;
import buysell.services.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name = "Order Controller", description = "API для управления заказами")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "Создать заказ", description = "Создает новый заказ")
    @ApiResponse(responseCode = "200", description = "Заказ успешно создан")
    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса")
    public GetOrderDto createOrder(@RequestBody CreateOrderDto createOrderDto) {
        return orderService.createOrder(createOrderDto);
    }

    @PostMapping("/bulk")
    @Operation(summary = "Создать несколько заказов",
        description = "Создает несколько заказов за один запрос")
    @ApiResponse(responseCode = "200", description = "Заказы успешно созданы")
    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса")
    public List<GetOrderDto> createBulkOrders(@RequestBody CreateBulkOrderDto createBulkOrderDto) {
        return orderService.createBulkOrders(createBulkOrderDto);
    }

    @GetMapping
    @Operation(summary = "Получить все заказы", description = "Возвращает список всех заказов")
    @ApiResponse(responseCode = "200", description = "Список заказов успешно получен")
    public List<GetOrderDto> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить заказ по ID",
        description = "Возвращает заказ по его идентификатору")
    @ApiResponse(responseCode = "200", description = "Заказ успешно найден")
    @ApiResponse(responseCode = "404", description = "Заказ не найден")
    public GetOrderDto getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Получить заказы пользователя",
        description = "Возвращает список заказов для указанного пользователя")
    @ApiResponse(responseCode = "200", description = "Список заказов успешно получен")
    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    public List<GetOrderDto> getOrdersByUser(@PathVariable Long userId) {
        return orderService.getOrdersByUser(userId);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Обновить статус заказа",
        description = "Обновляет статус заказа по его идентификатору")
    @ApiResponse(responseCode = "200", description = "Статус заказа успешно обновлен")
    @ApiResponse(responseCode = "404", description = "Заказ не найден")
    public GetOrderDto updateOrderStatus(@PathVariable Long id, @RequestParam Status status) {
        return orderService.updateOrderStatus(id, status);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить заказ",
        description = "Удаляет заказ по его идентификатору")
    @ApiResponse(responseCode = "200", description = "Заказ успешно удален")
    @ApiResponse(responseCode = "404", description = "Заказ не найден")
    public void deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
    }
}





