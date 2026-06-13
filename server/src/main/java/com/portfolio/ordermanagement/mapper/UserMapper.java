package com.portfolio.ordermanagement.mapper;

import com.portfolio.ordermanagement.dto.RegisterRequest;
import com.portfolio.ordermanagement.dto.UserResponse;
import com.portfolio.ordermanagement.entity.Role;
import com.portfolio.ordermanagement.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(RegisterRequest request){
        User user = new User();

        user.setEmail(request.email());
        user.setPassword(request.password());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setRole(Role.CUSTOMER);

        return user;
    }

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole()
        );
    }
}
