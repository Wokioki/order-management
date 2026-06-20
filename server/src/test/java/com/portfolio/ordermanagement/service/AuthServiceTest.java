package com.portfolio.ordermanagement.service;

import com.portfolio.ordermanagement.dto.*;
import com.portfolio.ordermanagement.entity.Role;
import com.portfolio.ordermanagement.entity.User;
import com.portfolio.ordermanagement.exception.EmailAlreadyExistsException;
import com.portfolio.ordermanagement.exception.InvalidCredentialsException;
import com.portfolio.ordermanagement.mapper.UserMapper;
import com.portfolio.ordermanagement.repository.UserRepository;
import com.portfolio.ordermanagement.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_shouldRegisterUserWhenEmailIsAvailable() {
        RegisterRequest request = new RegisterRequest(
                "test@example.com",
                "password123",
                "Test",
                "User"
        );

        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setRole(Role.CUSTOMER);

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setEmail("test@example.com");
        savedUser.setPassword("encoded-password");
        savedUser.setFirstName("Test");
        savedUser.setLastName("User");
        savedUser.setRole(Role.CUSTOMER);

        UserResponse expectedResponse = new UserResponse(
                1L,
                "test@example.com",
                "Test",
                "User",
                Role.CUSTOMER
        );

        when(userRepository.existsByEmail("test@example.com"))
                .thenReturn(false);

        when(userMapper.toEntity(request))
                .thenReturn(user);

        when(passwordEncoder.encode("password123"))
                .thenReturn("encoded-password");

        when(userRepository.save(user))
                .thenReturn(savedUser);

        when(userMapper.toResponse(savedUser))
                .thenReturn(expectedResponse);

        UserResponse result = authService.register(request);

        assertEquals(1L, result.id());
        assertEquals("test@example.com", result.email());
        assertEquals(Role.CUSTOMER, result.role());

        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(user);
    }

    @Test
    void register_shouldThrowExceptionWhenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest(
                "test@example.com",
                "password123",
                "Test",
                "User"
        );

        when(userRepository.existsByEmail("test@example.com"))
                .thenReturn(true);

        assertThrows(
                EmailAlreadyExistsException.class,
                () -> authService.register(request)
        );

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_shouldReturnAuthResponseWhenCredentialsAreValid() {
        LoginRequest request = new LoginRequest(
                "test@example.com",
                "password123"
        );

        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("encoded-password");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setRole(Role.CUSTOMER);

        UserResponse userResponse = new UserResponse(
                1L,
                "test@example.com",
                "Test",
                "User",
                Role.CUSTOMER
        );

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("password123", "encoded-password"))
                .thenReturn(true);

        when(jwtService.generateToken(user))
                .thenReturn("jwt-token");

        when(jwtService.getExpiration())
                .thenReturn(86400000L);

        when(userMapper.toResponse(user))
                .thenReturn(userResponse);

        AuthResponse result = authService.login(request);

        assertEquals("jwt-token", result.token());
        assertEquals("Bearer", result.tokenType());
        assertEquals(86400000L, result.expiresIn());
        assertEquals("test@example.com", result.user().email());
    }

    @Test
    void login_shouldThrowExceptionWhenEmailDoesNotExist() {
        LoginRequest request = new LoginRequest(
                "missing@example.com",
                "password123"
        );

        when(userRepository.findByEmail("missing@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(request)
        );

        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void login_shouldThrowExceptionWhenPasswordIsInvalid() {
        LoginRequest request = new LoginRequest(
                "test@example.com",
                "wrong-password"
        );

        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("encoded-password");

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("wrong-password", "encoded-password"))
                .thenReturn(false);

        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(request)
        );

        verify(jwtService, never()).generateToken(any(User.class));
    }

    @Test
    void getCurrentUser_shouldReturnCurrentUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setRole(Role.ADMIN);

        UserResponse expectedResponse = new UserResponse(
                1L,
                "test@example.com",
                "Test",
                "User",
                Role.ADMIN
        );

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        when(userMapper.toResponse(user))
                .thenReturn(expectedResponse);

        UserResponse result = authService.getCurrentUser("test@example.com");

        assertEquals(1L, result.id());
        assertEquals("test@example.com", result.email());
        assertEquals(Role.ADMIN, result.role());
    }
}