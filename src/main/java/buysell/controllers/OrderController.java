package buysell.controllers;

import buysell.dao.create.CreateOrderDto;
import buysell.dao.get.GetOrderDto;
import buysell.enums.Status;
import buysell.services.OrderService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public GetOrderDto createOrder(@RequestBody CreateOrderDto createOrderDto)
        throws BadRequestException {
        return orderService.createOrder(createOrderDto);
    }

    @GetMapping
    public List<GetOrderDto> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    public GetOrderDto getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    @GetMapping("/user/{userId}")
    public List<GetOrderDto> getOrdersByUser(@PathVariable Long userId) {
        return orderService.getOrdersByUser(userId);
    }

    @PutMapping("/{id}/status")
    public GetOrderDto updateOrderStatus(@PathVariable Long id, @RequestParam Status status) {
        return orderService.updateOrderStatus(id, status);
    }

    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
    }
}





