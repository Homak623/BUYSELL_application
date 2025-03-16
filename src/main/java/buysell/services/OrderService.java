package buysell.services;

import buysell.cache.CustomCache;
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
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
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
    private final CustomCache<Long, GetOrderDto> orderCache = new CustomCache<>(20000);

    @Transactional
    public GetOrderDto createOrder(CreateOrderDto createOrderDto) throws BadRequestException {
        User user = userRepository.findById(createOrderDto.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException(
                String.format(ErrorMessages.USER_NOT_FOUND, createOrderDto.getUserId())
            ));

        List<Long> productIds = createOrderDto.getProductIds();

        Set<Long> uniqueProductIds = new HashSet<>(productIds);

        Map<Long, Product> productMap = productRepository.findAllById(uniqueProductIds)
            .stream()
            .collect(Collectors.toMap(Product::getId, Function.identity()));

        List<Product> products = new ArrayList<>();
        for (Long productId : productIds) {
            Product product = productMap.get(productId);
            if (product == null) {
                throw new ResourceNotFoundException(
                    String.format(ErrorMessages.PRODUCT_NOT_FOUND, productId)
                );
            }
            products.add(product);
        }


        if (products.isEmpty()) {
            throw new BadRequestException(ErrorMessages.NO_VALID_PRODUCTS);
        }

        Order order = new Order(null, user, products, LocalDateTime.now(), Status.CREATED);
        return orderMapper.toDto(orderRepository.save(order));
    }

    public List<GetOrderDto> getAllOrders() {
        return orderMapper.toDtos(orderRepository.findAll());
    }

    public GetOrderDto getOrderById(Long id) {

        GetOrderDto cachedOrder = orderCache.get(id);
        if (cachedOrder != null) {
            return cachedOrder;
        }

        Order order = orderRepository.findWithProductsById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                String.format(ErrorMessages.ORDER_NOT_FOUND, id)
            ));

        GetOrderDto orderDto = orderMapper.toDto(order);

        orderCache.put(id, orderDto);

        return orderDto;
    }

    public List<GetOrderDto> getOrdersByUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException(
                String.format(ErrorMessages.USER_NOT_FOUND, userId)
            ));
        return orderMapper.toDtos(orderRepository.findByUser(user));
    }

    @Transactional
    public GetOrderDto updateOrderStatus(Long id, Status status) {
        Order order = orderRepository.findWithProductsById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                String.format(ErrorMessages.ORDER_NOT_FOUND, id)
            ));

        order.setStatus(status);
        GetOrderDto orderDto = orderMapper.toDto(orderRepository.save(order));

        orderCache.put(id, orderDto);

        return orderDto;
    }


    @Transactional
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                String.format(ErrorMessages.ORDER_NOT_FOUND, id)
            );
        }
        orderRepository.deleteById(id);

        orderCache.remove(id);
    }
}







