package com.portfolio.ordermanagement.dto;

import com.portfolio.ordermanagement.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(
        @NotNull(message = "Order status is required")
        OrderStatus status
) { }
