package com.portfolio.ordermanagement.service;

import com.portfolio.ordermanagement.dto.CreateOrderRequest;
import com.portfolio.ordermanagement.dto.OrderItemRequest;
import com.portfolio.ordermanagement.dto.OrderResponse;
import com.portfolio.ordermanagement.dto.UpdateOrderStatusRequest;
import com.portfolio.ordermanagement.entity.*;
import com.portfolio.ordermanagement.exception.InsufficientStockException;
import com.portfolio.ordermanagement.mapper.OrderMapper;
import com.portfolio.ordermanagement.repository.OrderRepository;
import com.portfolio.ordermanagement.repository.ProductRepository;
import com.portfolio.ordermanagement.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrder_shouldCreateOrderAndDecreaseStock() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        Product product = new Product();
        product.setId(1L);
        product.setName("Laptop Pro");
        product.setPrice(new BigDecimal("1499.99"));
        product.setStockQuantity(5);

        CreateOrderRequest request = new CreateOrderRequest(
                List.of(new OrderItemRequest(1L, 2))
        );

        OrderResponse expectedResponse = new OrderResponse(
                1L,
                1L,
                "test@example.com",
                OrderStatus.PENDING,
                new BigDecimal("2999.98"),
                List.of(),
                null,
                null
        );

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(orderMapper.toResponse(any(Order.class)))
                .thenReturn(expectedResponse);

        OrderResponse result = orderService.createOrder(request, "test@example.com");

        assertEquals(new BigDecimal("2999.98"), result.totalAmount());
        assertEquals(3, product.getStockQuantity());

        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void createOrder_shouldThrowExceptionWhenStockIsInsufficient() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        Product product = new Product();
        product.setId(1L);
        product.setName("Laptop Pro");
        product.setPrice(new BigDecimal("1499.99"));
        product.setStockQuantity(1);

        CreateOrderRequest request = new CreateOrderRequest(
                List.of(new OrderItemRequest(1L, 2))
        );

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        assertThrows(
                InsufficientStockException.class,
                () -> orderService.createOrder(request, "test@example.com")
        );

        assertEquals(1, product.getStockQuantity());

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void updateOrderStatus_shouldRestoreStockWhenOrderIsCancelled() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Laptop Pro");
        product.setStockQuantity(3);

        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.PAID);

        OrderItem item = new OrderItem();
        item.setProduct(product);
        item.setQuantity(2);

        order.addItem(item);

        UpdateOrderStatusRequest request =
                new UpdateOrderStatusRequest(OrderStatus.CANCELLED);

        OrderResponse expectedResponse = new OrderResponse(
                1L,
                1L,
                "test@example.com",
                OrderStatus.CANCELLED,
                new BigDecimal("2999.98"),
                List.of(),
                null,
                null
        );

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(orderMapper.toResponse(order))
                .thenReturn(expectedResponse);

        OrderResponse result = orderService.updateOrderStatus(1L, request);

        assertEquals(OrderStatus.CANCELLED, result.status());
        assertEquals(5, product.getStockQuantity());
    }

    @Test
    void updateOrderStatus_shouldNotRestoreStockTwiceWhenAlreadyCancelled() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Laptop Pro");
        product.setStockQuantity(3);

        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.CANCELLED);

        OrderItem item = new OrderItem();
        item.setProduct(product);
        item.setQuantity(2);

        order.addItem(item);

        UpdateOrderStatusRequest request =
                new UpdateOrderStatusRequest(OrderStatus.CANCELLED);

        OrderResponse expectedResponse = new OrderResponse(
                1L,
                1L,
                "test@example.com",
                OrderStatus.CANCELLED,
                new BigDecimal("2999.98"),
                List.of(),
                null,
                null
        );

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(orderMapper.toResponse(order))
                .thenReturn(expectedResponse);

        orderService.updateOrderStatus(1L, request);

        assertEquals(3, product.getStockQuantity());
    }
}