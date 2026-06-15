package com.portfolio.ordermanagement.service;

import com.portfolio.ordermanagement.dto.AuthResponse;
import com.portfolio.ordermanagement.dto.LoginRequest;
import com.portfolio.ordermanagement.dto.RegisterRequest;
import com.portfolio.ordermanagement.dto.UserResponse;
import com.portfolio.ordermanagement.entity.User;
import com.portfolio.ordermanagement.exception.EmailAlreadyExistsException;
import com.portfolio.ordermanagement.exception.InvalidCredentialsException;
import com.portfolio.ordermanagement.mapper.UserMapper;
import com.portfolio.ordermanagement.repository.UserRepository;
import com.portfolio.ordermanagement.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public UserResponse register(RegisterRequest request){
        if(userRepository.existsByEmail(request.email())){
            throw new EmailAlreadyExistsException(request.email());
        }

        User user = userMapper.toEntity(request);

        String encodedPassword = passwordEncoder.encode(request.password());
        user.setPassword(encodedPassword);

        User savedUser = userRepository.save(user);

        return userMapper.toResponse(savedUser);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request){

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(InvalidCredentialsException::new);

        boolean passwordMatches = passwordEncoder.matches(
                request.password(),
                user.getPassword()
        );

        if(!passwordMatches){
            throw new InvalidCredentialsException();
        }

        String token = jwtService.generateToken(user);

        return new AuthResponse(
                token,
                "Bearer",
                jwtService.getExpiration(),
                userMapper.toResponse(user)
        );
    }

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException());

        return userMapper.toResponse(user);
    }

}
