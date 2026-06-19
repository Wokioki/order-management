package com.portfolio.ordermanagement.controller;

import com.portfolio.ordermanagement.dto.CreateOrderRequest;
import com.portfolio.ordermanagement.dto.OrderResponse;
import com.portfolio.ordermanagement.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            Authentication authentication
    ) {
        return orderService.createOrder(
                request,
                authentication.getName()
        );
    }

    @GetMapping("/my")
    public List<OrderResponse> getMyOrders(Authentication authentication) {
        return orderService.getMyOrders(authentication.getName());
    }

    @GetMapping("/{id}")
    public OrderResponse getMyOrderById(
            @PathVariable Long id,
            Authentication authentication
    ) {
        return orderService.getMyOrderById(
                id,
                authentication.getName()
        );
    }
}
