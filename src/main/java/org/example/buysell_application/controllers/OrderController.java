package org.example.buysell_application.controllers;

import buysell.dao.dto.get.OrderGetDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.buysell_application.dao.dto.create.CreateOrderDto;
import org.example.buysell_application.dao.dto.get.GetOrderDto;
import org.example.buysell_application.dao.entityes.Order;
import org.example.buysell_application.dao.mappers.OrderMapper;
import org.example.buysell_application.services.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @PostMapping("/create")
    public GetOrderDto createOrder(@RequestBody CreateOrderDto createOrderDto) {
        Order order = orderService.createOrder(createOrderDto.getUserId(), createOrderDto.getProductIds());
        return orderMapper.toDto(order);
    }

    @GetMapping("/{id}")
    public GetOrderDto getOrderById(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        return orderMapper.toDto(order);
    }

    @GetMapping("/user/{userId}")
    public List<GetOrderDto> getOrdersByUser(@PathVariable Long userId) {
        List<Order> orders = orderService.getOrdersByUser(userId);
        return orderMapper.toDtos(orders);
    }

    @PutMapping("/{id}/status")
    public GetOrderDto updateOrderStatus(@PathVariable Long id, @RequestParam String status) {
        Order order = orderService.updateOrderStatus(id, status);
        return orderMapper.toDto(order);
    }

    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
    }
}



