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
import buysell.errors.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
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
    public GetOrderDto createOrder(CreateOrderDto createOrderDto) throws BadRequestException {
        User user = userRepository.findById(createOrderDto.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException(
                String.format(ErrorMessages.USER_NOT_FOUND, createOrderDto.getUserId())
            ));

        List<Product> products = productRepository.findAllById(createOrderDto.getProductIds());
        if (products.isEmpty()) {
            throw new BadRequestException(ErrorMessages.NO_VALID_PRODUCTS);
        }

        if (hasUserOrderedProducts(user, createOrderDto.getProductIds())) {
            throw new BadRequestException(ErrorMessages.PRODUCTS_ALREADY_ORDERED);
        }

        Order order = new Order(null, user, products, LocalDateTime.now(), Status.CREATED);
        return orderMapper.toDto(orderRepository.save(order));
    }

    private boolean hasUserOrderedProducts(User user, List<Long> productIds) {
        List<Order> userOrders = orderRepository.findByUser(user);
        return userOrders.stream()
            .flatMap(order -> order.getProducts().stream())
            .map(Product::getId)
            .anyMatch(productIds::contains);
    }

    public List<GetOrderDto> getAllOrders() {
        return orderMapper.toDtos(orderRepository.findAll());
    }

    public GetOrderDto getOrderById(Long id) {
        Order order = orderRepository.findWithProductsById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                String.format(ErrorMessages.ORDER_NOT_FOUND, id)
            ));
        return orderMapper.toDto(order);
    }

    public List<GetOrderDto> getOrdersByUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException(
                String.format(ErrorMessages.USER_NOT_FOUND, userId)
            ));
        return orderMapper.toDtos(orderRepository.findByUser(user));
    }

    public GetOrderDto updateOrderStatus(Long id, Status status) {
        Order order = orderRepository.findWithProductsById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                String.format(ErrorMessages.ORDER_NOT_FOUND, id)
            ));

        order.setStatus(status);
        return orderMapper.toDto(orderRepository.save(order));
    }

    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                String.format(ErrorMessages.ORDER_NOT_FOUND, id)
            );
        }
        orderRepository.deleteById(id);
    }
}







