package com.portfolio.ordermanagement.dto;

import com.portfolio.ordermanagement.entity.Role;

public record UserResponse(
    Long id,
    String email,
    String firstName,
    String lastName,
    Role role
) { }
