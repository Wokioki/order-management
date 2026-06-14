package com.portfolio.ordermanagement.service;

import com.portfolio.ordermanagement.dto.LoginRequest;
import com.portfolio.ordermanagement.dto.RegisterRequest;
import com.portfolio.ordermanagement.dto.UserResponse;
import com.portfolio.ordermanagement.entity.User;
import com.portfolio.ordermanagement.exception.EmailAlreadyExistsException;
import com.portfolio.ordermanagement.exception.InvalidCredentialsException;
import com.portfolio.ordermanagement.mapper.UserMapper;
import com.portfolio.ordermanagement.repository.UserRepository;
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
    public UserResponse login(LoginRequest request){

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(InvalidCredentialsException::new);

        boolean passwordMatches = passwordEncoder.matches(
                request.password(),
                user.getPassword()
        );

        if(!passwordMatches){
            throw new InvalidCredentialsException();
        }

        return userMapper.toResponse(user);
    }

}
