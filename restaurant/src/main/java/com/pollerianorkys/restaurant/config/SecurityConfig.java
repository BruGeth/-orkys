package com.pollerianorkys.restaurant.config;

import com.pollerianorkys.restaurant.security.SessionEventListener;
import com.pollerianorkys.restaurant.service.impl.UserDetailsServiceImpl;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionListener;
import java.io.IOException;
import java.util.logging.Logger;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private static final Logger logger = Logger.getLogger(SecurityConfig.class.getName());

    public SecurityConfig(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * Listener simple para eventos de sesión HTTP (solo logging)
     */
    @Bean
    public ServletListenerRegistrationBean<HttpSessionListener> sessionEventListener() {
        ServletListenerRegistrationBean<HttpSessionListener> listenerRegBean = 
            new ServletListenerRegistrationBean<>();
        listenerRegBean.setListener(new SessionEventListener());
        return listenerRegBean;
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
                .successHandler(this::handleAuthenticationSuccess)
                .failureUrl("/auth/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/auth/logout"))
                .logoutSuccessUrl("/auth/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .clearAuthentication(true)
                .permitAll()
            )
            .rememberMe(remember -> remember
                .key("Contraseña")
                .tokenValiditySeconds(86400) // 1 día
            )
            // Configuración simple de sesiones - SOLO timeout  
            .sessionManagement(session -> session
                .sessionFixation().changeSessionId() // Cambiar ID de sesión al hacer login (seguridad)
                .invalidSessionUrl("/auth/login?expired=true") // Redireccionar si sesión inválida
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

    /**
     * Configurar el AuthenticationManager con la estrategia de sesión
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        AuthenticationManager authManager = authConfig.getAuthenticationManager();
        return authManager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Handler personalizado para manejar autenticación exitosa.
     * SOLO registra el login básico.
     */
    private void handleAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                           Authentication authentication) throws ServletException, IOException {
        
        String username = authentication.getName();
        HttpSession currentSession = request.getSession();
        
        logger.info("🔐 Login exitoso para usuario: " + username);
        logger.info("🆔 Nueva sesión ID: " + currentSession.getId());
        logger.info("✅ Login completado para " + username);
        
        // Redirigir al home
        response.sendRedirect("/home");
    }
}
