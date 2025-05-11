package com.pollerianorkys.restaurant.service.impl;

import com.pollerianorkys.restaurant.dto.UserRegistrationDto;
import com.pollerianorkys.restaurant.model.User;
import com.pollerianorkys.restaurant.repository.UserRepository;
import com.pollerianorkys.restaurant.service.UserService;
import com.pollerianorkys.restaurant.util.CodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 *
 * @author Brunoo
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CodeGenerator codeGenerator;

    @Override
    public User registerUser(UserRegistrationDto dto) {
        User user = User.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .verificationCode(codeGenerator.generateCode())
                .enabled(false)
                .build();

        return userRepository.save(user);
    }
}
