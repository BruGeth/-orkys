package com.pollerianorkys.restaurant.config;

import com.pollerianorkys.restaurant.service.impl.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                // Recursos estáticos permitidos para todos
                    .requestMatchers("/css/**", "/js/**", "/img/**").permitAll()
                    // Páginas públicas
                    .requestMatchers("/", "/home", "/auth/login", "/auth/register", "/auth/verify",
                            "/auth/resend-token", "/complaints/book", "/libro-reclamaciones",
                            "/carta", "/menu/carta", "/promociones", "/locales",
                            "/locations", "/promotions", "/nosotros", "/trabaja-con-nosotros",
                            "/terminos-promociones", "/about/**",
                    // Rutas públicas para recuperación de contraseña
                    "/auth/forgot-password",
                    "/auth/reset-password-token",
                    "/auth/verify-reset-token",
                    "/auth/resend-reset-token",
                    "/auth/change-password"
            ).permitAll()
                    .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                    // Páginas que requieren autenticación
                    .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/login")
                .defaultSuccessUrl("/home", true)
                .failureUrl("/auth/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/auth/logout"))
                .logoutSuccessUrl("/auth/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .rememberMe(remember -> remember
                .key("Contraseña")
                .tokenValiditySeconds(86400) // 1 día
            );

        return http.build();
    }


    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
