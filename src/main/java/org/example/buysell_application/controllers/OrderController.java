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

    @PostMapping("/create")
    public GetOrderDto createOrder(@RequestBody CreateOrderDto createOrderDto) {
        return orderService.createOrder(createOrderDto);
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
    public GetOrderDto updateOrderStatus(@PathVariable Long id, @RequestParam String status) {
        return orderService.updateOrderStatus(id, status);
    }

    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
    }
}




