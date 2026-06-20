package com.portfolio.ordermanagement.service;

import com.portfolio.ordermanagement.dto.CreateOrderRequest;
import com.portfolio.ordermanagement.dto.OrderItemRequest;
import com.portfolio.ordermanagement.dto.OrderResponse;
import com.portfolio.ordermanagement.dto.UpdateOrderStatusRequest;
import com.portfolio.ordermanagement.entity.*;
import com.portfolio.ordermanagement.exception.InsufficientStockException;
import com.portfolio.ordermanagement.exception.InvalidCredentialsException;
import com.portfolio.ordermanagement.exception.OrderNotFoundException;
import com.portfolio.ordermanagement.exception.ProductNotFoundException;
import com.portfolio.ordermanagement.mapper.OrderMapper;
import com.portfolio.ordermanagement.repository.OrderRepository;
import com.portfolio.ordermanagement.repository.ProductRepository;
import com.portfolio.ordermanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new InvalidCredentialsException());

        Order order = new Order();
        order.setUser(user);

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : request.items()) {
            Product product = productRepository.findById(itemRequest.productId())
                    .orElseThrow(() -> new ProductNotFoundException(itemRequest.productId()));

            if (product.getStockQuantity() < itemRequest.quantity()) {
                throw new InsufficientStockException(
                        product.getName(),
                        itemRequest.quantity(),
                        product.getStockQuantity()
                );
            }

            BigDecimal lineTotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(itemRequest.quantity()));

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setProductName(product.getName());
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setQuantity(itemRequest.quantity());
            orderItem.setLineTotal(lineTotal);

            order.addItem(orderItem);

            product.setStockQuantity(
                    product.getStockQuantity() - itemRequest.quantity()
            );

            totalAmount = totalAmount.add(lineTotal);
        }

        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);

        return orderMapper.toResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders(String userEmail){
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new InvalidCredentialsException());

        return orderRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse getMyOrderById(Long id, String userEmail){
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new InvalidCredentialsException());

        Order order = orderRepository.findByIdAndUser(id,user)
                .orElseThrow(() -> new OrderNotFoundException(id));

        return orderMapper.toResponse(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(orderMapper::toResponse);
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long id, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        if (request.status() == OrderStatus.CANCELLED
                && order.getStatus() != OrderStatus.CANCELLED) {

            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();

                product.setStockQuantity(
                        product.getStockQuantity() + item.getQuantity()
                );
            }
        }

        order.setStatus(request.status());

        return orderMapper.toResponse(order);
    }
}
