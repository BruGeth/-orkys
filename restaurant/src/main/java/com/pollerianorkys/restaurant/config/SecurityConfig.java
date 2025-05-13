package com.pollerianorkys.restaurant.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                // Rutas públicas
                .requestMatchers(
                        "/",
                        "/login",
                        "/register",
                        "/libro-reclamaciones",
                        "/css/**",
                        "/js/**",
                        "/img/**",
                        "/webjars/**"
                ).permitAll()
                // Resto requiere autenticación
                .anyRequest().authenticated()
                )
                .formLogin(form -> form
                // 🟢 Redirección al login (si no está autenticado) a tu propio HTML
                .loginPage("/login")
                .permitAll()
                )
                .logout(logout -> logout
                .logoutSuccessUrl("/?logout") // Puedes volver al home tras cerrar sesión
                .permitAll()
                )
                .build();
    }

    // Encoder para hashear contraseñas
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
