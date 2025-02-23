package buysell.services;

import buysell.dao.create.CreateOrderDto;
import buysell.dao.entityes.Order;
import buysell.dao.entityes.Product;
import buysell.dao.entityes.User;
import buysell.dao.get.GetOrderDto;
import buysell.dao.mappers.OrderMapper;
import buysell.dao.repository.OrderRepository;
import buysell.dao.repository.ProductRepository;
import buysell.dao.repository.UserRepository;
import buysell.enums.Status;
import buysell.errors.ErrorMessages;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    @Transactional
    public GetOrderDto createOrder(CreateOrderDto createOrderDto) {
        User user = userRepository.findById(createOrderDto.getUserId())
            .orElseThrow(() -> new NoSuchElementException(
                String.format(ErrorMessages.USER_NOT_FOUND, createOrderDto.getUserId())
            ));

        List<Product> products = productRepository.findAllById(createOrderDto.getProductIds());
        if (products.isEmpty()) {
            throw new IllegalArgumentException(ErrorMessages.NO_VALID_PRODUCTS);
        }

        Order order = new Order(null, user, products, LocalDateTime.now(), Status.CREATED);
        return orderMapper.toDto(orderRepository.save(order));
    }

    public GetOrderDto getOrderById(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException(
                String.format(ErrorMessages.ORDER_NOT_FOUND, id)
            ));
        return orderMapper.toDto(order);
    }

    public List<GetOrderDto> getOrdersByUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException(
                String.format(ErrorMessages.USER_NOT_FOUND, userId)
            ));
        return orderMapper.toDtos(orderRepository.findByUser(user));
    }

    @Transactional
    public GetOrderDto updateOrderStatus(Long id, Status status) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException(
                String.format(ErrorMessages.ORDER_NOT_FOUND, id)
            ));

        order.setStatus(status);
        return orderMapper.toDto(orderRepository.save(order));
    }

    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new NoSuchElementException(
                String.format(ErrorMessages.ORDER_NOT_FOUND, id)
            );
        }
        orderRepository.deleteById(id);
    }
}






