package org.example.buysell_application.services;

import buysell.dao.mappers.OrderMapper;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.example.buysell_application.dao.dto.create.CreateOrderDto;
import org.example.buysell_application.dao.dto.get.GetOrderDto;
import org.example.buysell_application.dao.entityes.Order;
import org.example.buysell_application.dao.entityes.Product;
import org.example.buysell_application.dao.entityes.User;
import org.example.buysell_application.dao.repository.OrderRepository;
import org.example.buysell_application.dao.repository.ProductRepository;
import org.example.buysell_application.dao.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    public GetOrderDto createOrder(CreateOrderDto createOrderDto) {
        User user = userRepository.findById(createOrderDto.getUserId())
            .orElseThrow(() -> new NoSuchElementException("User with id " + createOrderDto.getUserId() + " not found"));

        List<Product> products = productRepository.findAllById(createOrderDto.getProductIds());
        if (products.isEmpty()) {
            throw new IllegalArgumentException("No valid products found for the given IDs");
        }

        Order order = new Order(null, user, products, LocalDateTime.now(), "NEW");
        return orderMapper.toDto(orderRepository.save(order));
    }

    public GetOrderDto getOrderById(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Order with id " + id + " not found"));
        return orderMapper.toDto(order);
    }

    public List<GetOrderDto> getOrdersByUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
        List<Order> orders = orderRepository.findByUser(user);
        return orderMapper.toDtos(orders);
    }

    public GetOrderDto updateOrderStatus(Long id, String status) {
        return orderRepository.findById(id).map(order -> {
            order.setStatus(status);
            return orderMapper.toDto(orderRepository.save(order));
        }).orElseThrow(() -> new NoSuchElementException("Order with id " + id + " not found"));
    }

    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Order with id " + id + " not found"));
        orderRepository.delete(order);
    }
}




