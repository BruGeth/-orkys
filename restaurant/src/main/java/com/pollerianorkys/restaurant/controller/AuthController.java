package com.pollerianorkys.restaurant.controller;

import com.pollerianorkys.restaurant.dto.LoginRequestDto;
import com.pollerianorkys.restaurant.dto.UserRegistrationDto;
import com.pollerianorkys.restaurant.model.User;
import com.pollerianorkys.restaurant.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador para manejar la autenticación de usuarios (registro, login)
 */
@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    /**
     * Muestra la página de inicio de sesión
     */
    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("loginRequest", new LoginRequestDto());
        return "login";
    }

    /**
     * Procesa el formulario de inicio de sesión
     */
    @PostMapping("/login")
    public String processLogin(@Valid @ModelAttribute("loginRequest") LoginRequestDto loginRequest,
                              BindingResult result,
                              HttpServletRequest request,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "login";
        }
        
        try {
            // Intentar autenticar al usuario
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                )
            );
            
            // Si la autenticación es exitosa, establecer la autenticación en el contexto
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // Manejar "Recordarme" si está habilitado
            if (loginRequest.isRememberMe()) {
                // La configuración de "remember-me" se maneja en SecurityConfig
            }
            
            redirectAttributes.addFlashAttribute("success", "¡Inicio de sesión exitoso!");
            return "redirect:/home";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Credenciales inválidas. Por favor, intenta nuevamente.");
            return "redirect:/auth/login";
        }
    }

    /**
     * Muestra el formulario de registro
     */
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("userRegistration", new UserRegistrationDto());
        return "register";
    }

    /**
     * Procesa el formulario de registro
     */
    @PostMapping("/register")
    public String processRegistration(@Valid @ModelAttribute("userRegistration") UserRegistrationDto registrationDto, 
                                     BindingResult result,
                                     RedirectAttributes redirectAttributes,
                                     HttpServletRequest request) {
        // Verificar si hay errores de validación
        if (result.hasErrors()) {
            return "register";
        }
        
        try {
            // Registrar usuario usando el DTO directamente
            User registeredUser = userService.registerUser(registrationDto);
            
            // Autenticar automáticamente al usuario después del registro
            try {
                // Autologin usando Spring Security
                request.login(registrationDto.getUsername(), registrationDto.getPassword());
                
                redirectAttributes.addFlashAttribute("success", 
                    "¡Registro exitoso! Has iniciado sesión automáticamente.");
                return "redirect:/home";
            } catch (ServletException e) {
                // Si falla el autologin, redirigir al login
                redirectAttributes.addFlashAttribute("success", 
                    "¡Registro exitoso! Por favor, inicia sesión con tus credenciales.");
                return "redirect:/auth/login";
            }
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/auth/register";
        }
    }
    
    /**
     * Procesa el cierre de sesión
     */
    @PostMapping("/logout")
    public String logout(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        try {
            request.logout();
            redirectAttributes.addFlashAttribute("success", "Has cerrado sesión correctamente.");
        } catch (ServletException e) {
            // Manejar error de cierre de sesión
        }
        return "redirect:/auth/login";
    }
}
