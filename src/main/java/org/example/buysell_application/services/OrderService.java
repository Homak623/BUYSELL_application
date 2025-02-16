package org.example.buysell_application.services;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
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

    public Order createOrder(Long userId, List<Long> productIds) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        List<Product> products = productRepository.findAllById(productIds);
        if (products.isEmpty()) {
            throw new IllegalArgumentException("No valid products found for the given IDs");
        }

        Order order = new Order(null, user, products, LocalDateTime.now(), "NEW");
        return orderRepository.save(order);
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Order with id " + id + " not found"));
    }

    public List<Order> getOrdersByUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
        return orderRepository.findByUser(user);
    }

    public Order updateOrderStatus(Long id, String status) {
        return orderRepository.findById(id).map(order -> {
            order.setStatus(status);
            return orderRepository.save(order);
        }).orElseThrow(() -> new NoSuchElementException("Order with id " + id + " not found"));
    }

    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Order with id " + id + " not found"));
        orderRepository.delete(order);
    }
}

