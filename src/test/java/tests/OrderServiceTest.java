package tests;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import buysell.cache.CustomCache;
import buysell.dao.create.CreateBulkOrderDto;
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
import buysell.errors.BadRequestException;
import buysell.errors.ResourceNotFoundException;
import buysell.services.OrderService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private CustomCache<Long, GetOrderDto> orderCache;

    @InjectMocks
    private OrderService orderService;

    private User user;
    private Product product;
    private Order order;
    private CreateOrderDto createOrderDto;
    private GetOrderDto getOrderDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        product = new Product();
        product.setId(1L);

        order = new Order();
        order.setId(1L);
        order.setUser(user);
        order.setProducts(List.of(product));
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus(Status.CREATED);

        createOrderDto = new CreateOrderDto();
        createOrderDto.setUserId(1L);
        createOrderDto.setProductIds(List.of(1L));

        getOrderDto = new GetOrderDto();
        getOrderDto.setId(1L);
        getOrderDto.setUserId(1L);
        getOrderDto.setProductIds(List.of(1L));
        getOrderDto.setCreatedAt(LocalDateTime.now());
        getOrderDto.setStatus(Status.CREATED);
    }

    @Test
    void createBulkOrders_Success() {
        // Arrange
        CreateBulkOrderDto bulkOrderDto = new CreateBulkOrderDto();
        bulkOrderDto.setOrders(List.of(createOrderDto));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findAllById(anySet())).thenReturn(List.of(product));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.toDto(order)).thenReturn(getOrderDto);

        List<GetOrderDto> result = orderService.createBulkOrders(bulkOrderDto);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        verify(userRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).findAllById(anySet());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void createBulkOrders_EmptyList() {

        CreateBulkOrderDto bulkOrderDto = new CreateBulkOrderDto();
        bulkOrderDto.setOrders(List.of());

        List<GetOrderDto> result = orderService.createBulkOrders(bulkOrderDto);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void createOrder_NoValidProducts() {

        CreateOrderDto order = new CreateOrderDto();
        createOrderDto.setUserId(1L);
        createOrderDto.setProductIds(List.of());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findAllById(anySet())).thenReturn(List.of());

        assertThrows(BadRequestException.class, () -> orderService.createOrder(order));
        verify(userRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).findAllById(anySet());
    }

    @Test
    void getAllOrders_EmptyList() {

        when(orderRepository.findAll()).thenReturn(List.of());
        when(orderMapper.toDtos(List.of())).thenReturn(List.of());

        List<GetOrderDto> result = orderService.getAllOrders();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(orderRepository, times(1)).findAll();
        verify(orderMapper, times(1)).toDtos(List.of());
    }

    @Test
    void getOrderById_CacheHit() {

        Long orderId = 1L;
        when(orderCache.get(orderId)).thenReturn(getOrderDto);

        GetOrderDto result = orderService.getOrderById(orderId);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(orderCache, times(1)).get(orderId);
        verify(orderRepository, never()).findWithProductsById(anyLong());
    }

    @Test
    void getOrderById_CacheMiss() {
        Long orderId = 1L;
        when(orderCache.get(orderId)).thenReturn(null);
        when(orderRepository.findWithProductsById(orderId)).thenReturn(Optional.of(order));
        when(orderMapper.toDto(order)).thenReturn(getOrderDto);

        GetOrderDto result = orderService.getOrderById(orderId);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(orderCache, times(1)).get(orderId);
        verify(orderRepository, times(1)).findWithProductsById(orderId);
        verify(orderCache, times(1)).put(orderId, getOrderDto);
    }

    @Test
    void createOrder_Success() throws BadRequestException {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findAllById(anySet())).thenReturn(List.of(product));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.toDto(order)).thenReturn(getOrderDto);

        GetOrderDto result = orderService.createOrder(createOrderDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).findAllById(anySet());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void deleteOrder_Success() {

        Long orderId = 1L;
        when(orderRepository.existsById(orderId)).thenReturn(true);

        orderService.deleteOrder(orderId);

        verify(orderRepository, times(1)).existsById(orderId);
        verify(orderRepository, times(1)).deleteById(orderId);
        verify(orderCache, times(1)).remove(orderId);
    }

    @Test
    void createOrder_UserNotFound() {

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> orderService.createOrder(createOrderDto));
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void createOrder_ProductNotFound() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findAllById(anySet())).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class,
            () -> orderService.createOrder(createOrderDto));
        verify(userRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).findAllById(anySet());
    }

    @Test
    void getAllOrders_Success() {

        when(orderRepository.findAll()).thenReturn(List.of(order));
        when(orderMapper.toDtos(List.of(order))).thenReturn(List.of(getOrderDto));

        List<GetOrderDto> result = orderService.getAllOrders();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        verify(orderRepository, times(1)).findAll();
        verify(orderMapper, times(1)).toDtos(List.of(order));
    }

    @Test
    void deleteOrder_OrderNotFound() {

        when(orderRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> orderService.deleteOrder(1L));
        verify(orderRepository, times(1)).existsById(1L);
        verify(orderRepository, never()).deleteById(anyLong());
        verify(orderCache, never()).remove(anyLong());
    }

    @Test
    void getOrdersByUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(orderRepository.findByUser(user)).thenReturn(List.of(order));
        when(orderMapper.toDtos(List.of(order))).thenReturn(List.of(getOrderDto));

        List<GetOrderDto> result = orderService.getOrdersByUser(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        verify(userRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).findByUser(user);
        verify(orderMapper, times(1)).toDtos(List.of(order));
    }

    @Test
    void getOrdersByUser_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.getOrdersByUser(1L));
        verify(userRepository, times(1)).findById(1L);
        verify(orderRepository, never()).findByUser(any(User.class));
    }

    @Test
    void updateOrderStatus_Success() {
        when(orderRepository.findWithProductsById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toDto(order)).thenReturn(getOrderDto);

        GetOrderDto result = orderService.updateOrderStatus(1L, Status.SHIPPED);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(orderRepository, times(1)).findWithProductsById(1L);
        verify(orderRepository, times(1)).save(order);
        verify(orderMapper, times(1)).toDto(order);
    }

    @Test
    void updateOrderStatus_OrderNotFound() {
        when(orderRepository.findWithProductsById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
            () -> orderService.updateOrderStatus(1L, Status.SHIPPED));
        verify(orderRepository, times(1)).findWithProductsById(1L);
        verify(orderRepository, never()).save(any(Order.class));
        verify(orderCache, never()).put(anyLong(), any(GetOrderDto.class));
    }
}
