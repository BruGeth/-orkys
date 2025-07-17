package com.pollerianorkys.restaurant.service;

import com.pollerianorkys.restaurant.dto.PasswordChangeDto;
import com.pollerianorkys.restaurant.dto.UserProfileDto;
import com.pollerianorkys.restaurant.dto.UserRegistrationDto;
import com.pollerianorkys.restaurant.model.User;

import java.util.Optional;

public interface UserService {
    
    User registerUser(UserRegistrationDto registrationDto);
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByUsername(String username);
    
    boolean existsByEmail(String email);
    
    boolean existsByUsername(String username);
    
    boolean existsByPhone(String phone);
    
    void updateProfile(String username, UserProfileDto profileDto);
    
    boolean changePassword(String username, String currentPassword, String newPassword);

    Optional<User> findByToken(String token);
    void save(User user);

}
