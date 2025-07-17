package com.pollerianorkys.restaurant.service.impl;

import com.pollerianorkys.restaurant.dto.UserProfileDto;
import com.pollerianorkys.restaurant.dto.UserRegistrationDto;
import com.pollerianorkys.restaurant.model.Authority;
import com.pollerianorkys.restaurant.model.User;
import com.pollerianorkys.restaurant.repository.AuthorityRepository;
import com.pollerianorkys.restaurant.repository.UserRepository;
import com.pollerianorkys.restaurant.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthorityRepository authorityRepository;


    @Override
    @Transactional
    public User registerUser(UserRegistrationDto registrationDto) {
        // Verificar si el email, username o teléfono ya existen
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new IllegalArgumentException("El correo electrónico ya está registrado");
        }
        
        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            throw new IllegalArgumentException("El nombre de usuario ya está en uso");
        }
        
        if (userRepository.existsByPhone(registrationDto.getPhone())) {
            throw new IllegalArgumentException("El número de teléfono ya está registrado");
        }
        
        Authority rolDefault = authorityRepository.findById(2L).get();

        // Genera token y expiración
        String token = String.valueOf(new java.util.Random().nextInt(900000) + 100000);
        LocalDateTime tokenExpiry = LocalDateTime.now().plusSeconds(30); // Tiempo de expiración del token (30 segundos)

        // Crear el usuario a partir del DTO
        User user = User.builder()
                .firstName(registrationDto.getFirstName())
                .lastName(registrationDto.getLastName())
                .email(registrationDto.getEmail())
                .phone(registrationDto.getPhone())
                .username(registrationDto.getUsername())
                .password(passwordEncoder.encode(registrationDto.getPassword()))
                .authority(rolDefault)
                .verified(false) // Usuario no verificado
                .verificationToken(token)
                .tokenExpiry(tokenExpiry) // Asigna el token y su expiración
                .build();
        
        // Guardar el usuario
        userRepository.save(user);
        return user;
    }

    @Override
    public Optional<User> findByToken(String token) { return userRepository.findByVerificationToken(token);}

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    @Override
    public boolean existsByPhone(String phone) {
        return userRepository.existsByPhone(phone);
    }
    
    @Override
    @Transactional
    public void updateProfile(String username, UserProfileDto profileDto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        
        // Verificar si el email ya está en uso por otro usuario
        if (!user.getEmail().equals(profileDto.getEmail()) && 
            userRepository.existsByEmail(profileDto.getEmail())) {
            throw new IllegalArgumentException("El correo electrónico ya está en uso por otro usuario");
        }
        
        // Verificar si el teléfono ya está en uso por otro usuario
        if (!user.getPhone().equals(profileDto.getPhone()) && 
            userRepository.existsByPhone(profileDto.getPhone())) {
            throw new IllegalArgumentException("El número de teléfono ya está en uso por otro usuario");
        }
        
        // Actualizar datos
        user.setFirstName(profileDto.getFirstName());
        user.setLastName(profileDto.getLastName());
        user.setEmail(profileDto.getEmail());
        user.setPhone(profileDto.getPhone());
        
        userRepository.save(user);
    }
    
    @Override
    @Transactional
    public boolean changePassword(String username, String currentPassword, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        
        // Verificar que la contraseña actual sea correcta
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return false;
        }
        
        // Actualizar contraseña
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        return true;
    }
}
