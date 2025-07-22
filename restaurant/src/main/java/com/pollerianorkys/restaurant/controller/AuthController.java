package com.pollerianorkys.restaurant.controller;

import com.pollerianorkys.restaurant.dto.LoginRequestDto;
import com.pollerianorkys.restaurant.dto.UserRegistrationDto;
import com.pollerianorkys.restaurant.model.User;
import com.pollerianorkys.restaurant.service.EmailService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Controlador para manejar la autenticación de usuarios (registro, login)
 */
@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    /**
     * Muestra la página de inicio de sesión
     */
    @GetMapping("/login")
    public String showLoginPage(Model model, HttpServletRequest request) {
        model.addAttribute("loginRequest", new LoginRequestDto());
        
        // Manejar diferentes tipos de mensajes de sesión
        String timeout = request.getParameter("timeout");
        String expired = request.getParameter("expired");
        String invalid = request.getParameter("invalid");
        String logout = request.getParameter("logout");
        String error = request.getParameter("error");
        
        if (timeout != null) {
            model.addAttribute("timeoutMessage", 
                "Tu sesión ha expirado por inactividad (1 minuto). Por favor, inicia sesión nuevamente.");
        } else if (expired != null) {
            model.addAttribute("expiredMessage", 
                "Tu sesión ha expirado. Por favor, inicia sesión nuevamente.");
        } else if (invalid != null) {
            model.addAttribute("invalidMessage", 
                "Sesión inválida. Por favor, inicia sesión nuevamente.");
        } else if (logout != null) {
            model.addAttribute("logoutMessage", 
                "Has cerrado sesión correctamente.");
        } else if (error != null) {
            model.addAttribute("errorMessage", 
                "Credenciales inválidas. Por favor, verifica tu usuario y contraseña.");
        }
        
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
            // Registrar al usuario con campos de verificación
            User registeredUser = userService.registerUser(registrationDto);

            // Enviar correo de verificación
            emailService.sendVerificationEmail(
                    registeredUser.getEmail(),
                    registeredUser.getVerificationToken()
            );

            // Mensaje y redirección a verificación
            redirectAttributes.addFlashAttribute("success",
                    "¡Registro exitoso! Verifica tu cuenta desde el correo.");
            redirectAttributes.addFlashAttribute("email", registeredUser.getEmail());

            return "redirect:/auth/verify";

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

    /**
     * Muestra el formulario de verificación de email
     */
    @GetMapping("/verify")
    public String showVerificationForm(@ModelAttribute("email") String email, Model model) {
        model.addAttribute("email", email != null ? email : "");

        // Si el email ya está proporcionado, buscamos al usuario
        userService.findByEmail(email).ifPresent(user -> model.addAttribute("user", user));
        return "verify";
    }

    /**
      Procesa la verificación de email
     */
    @PostMapping("/verify")
    public String processVerification(@RequestParam("token") String token,
                                      @RequestParam("email") String email,
                                      RedirectAttributes redirectAttributes) {
        Optional<User> optionalUser = userService.findByToken(token);

        // Verifica si el usuario existe y si el token es válido
        if (optionalUser.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Token inválido.");
            redirectAttributes.addFlashAttribute("email", email); // Mantener el email para reenvío
            return "redirect:/auth/verify";
        }



        User user = optionalUser.get();
        // Verifica si el usuario ya está verificado
        if (user.getTokenExpiry().isBefore(LocalDateTime.now())) {
            redirectAttributes.addFlashAttribute("error", "Token expirado. Reenvía uno nuevo.");
            redirectAttributes.addFlashAttribute("email", user.getEmail()); // Mantener el email para reenvío
            return "redirect:/auth/verify";
        }


        // Validación exitosa: activar cuenta
        user.setVerified(true);
        user.setVerificationToken(null);
        user.setTokenExpiry(null);
        user.setResendAttempts(0); // Reiniciar intentos de reenvío
        userService.save(user); // Guardar cambios en la base de datos

        redirectAttributes.addFlashAttribute("success", "¡Cuenta verificada! Ya puedes iniciar sesión.");
        return "redirect:/auth/login";
    }

    /**
     * Reenvía el token de verificación al correo del usuario
     */
    @PostMapping("/resend-token")
    public String resendVerificationToken(@RequestParam("email") String email, RedirectAttributes redirectAttributes) {
        Optional<User> optionalUser = userService.findByEmail(email);

        // Verifica si el usuario existe
        if (optionalUser.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Correo no registrado.");
            return "redirect:/auth/verify";
        }

        // Obtiene el usuario
        User user = optionalUser.get();

        // Si ya está verificado, no necesita nuevo código
        if (user.isVerified()) {
            redirectAttributes.addFlashAttribute("success", "Tu cuenta ya está verificada.");
            return "redirect:/auth/login";
        }

        // Verifica si ha alcanzado el límite de reenvíos
        if (user.getResendAttempts() >= 3) {
            redirectAttributes.addFlashAttribute("error", "Has alcanzado el límite de reenvíos.");
            redirectAttributes.addFlashAttribute("email", email);
            return "redirect:/auth/verify";
        }

        //  Generamos nuevo token y tiempo de expiración
        String newToken = String.valueOf(new java.util.Random().nextInt(900000) + 100000); //  código de 6 dígitos
        LocalDateTime newExpiry = LocalDateTime.now().plusSeconds(30); // 30 segundos de expiración

        // Actualiza el usuario con el nuevo token y expiración
        user.setResendAttempts(user.getResendAttempts() + 1);
        user.setVerificationToken(newToken);
        user.setTokenExpiry(newExpiry);
        userService.save(user);

        //  Reenvía el nuevo código
        emailService.sendVerificationEmail(user.getEmail(), newToken);

        redirectAttributes.addFlashAttribute("success", "Nuevo código enviado a tu correo.");
        redirectAttributes.addFlashAttribute("email", email);
        return "redirect:/auth/verify";
    }

    /**
     * Muestra la página de recuperación de contraseña
     */
    @GetMapping("/forgot-password")
    public String showForgotPasswordPage() {
        return "fragments/forgot-password";
    }

    /**
     * Procesa el formulario de recuperación de contraseña
     */
    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, RedirectAttributes redirectAttributes) {
        Optional<User> optionalUser = userService.findByEmail(email);
        if (optionalUser.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "No existe una cuenta con ese correo electrónico.");
            return "redirect:/auth/forgot-password";
        }
        User user = optionalUser.get();
        // Reiniciar el contador de reenvíos al iniciar el proceso
        user.setResendAttempts(0);
        // Generar código de 6 dígitos
        String resetToken = String.valueOf((int)(Math.random() * 900000) + 100000);
        user.setResetToken(resetToken);
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(10));
        userService.save(user);
        // Enviar correo
        emailService.sendPasswordResetEmail(user.getEmail(), resetToken);
        // Redirigir directamente a la página de ingreso de código
        redirectAttributes.addFlashAttribute("success", "Se ha enviado un código de recuperación a tu correo electrónico.");
        redirectAttributes.addFlashAttribute("resendAttempts", 0);
        return "redirect:/auth/reset-password-token?email=" + email;
    }

    /**
     * Muestra la página para ingresar el código de recuperación
     */
    @GetMapping("/reset-password-token")
    public String showResetTokenPage(@RequestParam("email") String email, Model model, @ModelAttribute("resendAttempts") Integer resendAttempts) {
        model.addAttribute("email", email);
        if (resendAttempts != null) {
            model.addAttribute("resendAttempts", resendAttempts);
        } else {
            Optional<User> optionalUser = userService.findByEmail(email);
            model.addAttribute("resendAttempts", optionalUser.map(User::getResendAttempts).orElse(0));
        }
        return "fragments/reset-password-token";
    }

    /**
     * Procesa el código de recuperación ingresado
     */
    @PostMapping("/verify-reset-token")
    public String verifyResetToken(@RequestParam("email") String email,
                                   @RequestParam("resetToken") String resetToken,
                                   RedirectAttributes redirectAttributes) {
        Optional<User> optionalUser = userService.findByEmail(email);
        if (optionalUser.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "No existe una cuenta con ese correo electrónico.");
            return "redirect:/auth/forgot-password";
        }
        User user = optionalUser.get();
        if (user.getResetToken() == null || user.getResetTokenExpiry() == null ||
            !user.getResetToken().equals(resetToken) ||
            user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            redirectAttributes.addFlashAttribute("error", "Código inválido o expirado. Solicita uno nuevo.");
            redirectAttributes.addFlashAttribute("email", email);
            redirectAttributes.addFlashAttribute("resendAttempts", user.getResendAttempts());
            return "redirect:/auth/reset-password-token?email=" + email;
        }
        // Aquí podrías redirigir a una página para cambiar la contraseña
        redirectAttributes.addFlashAttribute("email", email);
        redirectAttributes.addFlashAttribute("resetToken", resetToken);
        return "redirect:/auth/change-password";
    }

    /**
     * Reenvía el código de recuperación
     */
    @PostMapping("/resend-reset-token")
    public String resendResetToken(@RequestParam("email") String email, RedirectAttributes redirectAttributes) {
        Optional<User> optionalUser = userService.findByEmail(email);
        if (optionalUser.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "No existe una cuenta con ese correo electrónico.");
            return "redirect:/auth/forgot-password";
        }
        User user = optionalUser.get();
        // Límite de reenvíos: 3
        if (user.getResendAttempts() >= 3) {
            redirectAttributes.addFlashAttribute("error", "Has alcanzado el límite de 3 reenvíos de código.");
            redirectAttributes.addFlashAttribute("email", email);
            redirectAttributes.addFlashAttribute("resendAttempts", user.getResendAttempts());
            return "redirect:/auth/reset-password-token?email=" + email;
        }
        user.setResendAttempts(user.getResendAttempts() + 1);
        String resetToken = String.valueOf((int)(Math.random() * 900000) + 100000);
        user.setResetToken(resetToken);
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(10));
        userService.save(user);
        emailService.sendPasswordResetEmail(user.getEmail(), resetToken);
        redirectAttributes.addFlashAttribute("success", "Se ha reenviado el código de recuperación a tu correo electrónico.");
        redirectAttributes.addFlashAttribute("email", email);
        redirectAttributes.addFlashAttribute("resendAttempts", user.getResendAttempts());
        return "redirect:/auth/reset-password-token?email=" + email;
    }

    /**
     * Muestra el formulario para cambiar la contraseña
     */
    @GetMapping("/change-password")
    public String showChangePasswordPage(@ModelAttribute("email") String email,
                                         @ModelAttribute("resetToken") String resetToken,
                                         Model model) {
        model.addAttribute("email", email);
        model.addAttribute("resetToken", resetToken);
        return "fragments/change-password";
    }

    /**
     * Procesa el cambio de contraseña
     */
    @PostMapping("/change-password")
    public String processChangePassword(@RequestParam("email") String email,
                                        @RequestParam("resetToken") String resetToken,
                                        @RequestParam("password") String password,
                                        @RequestParam("confirmPassword") String confirmPassword,
                                        RedirectAttributes redirectAttributes) {
        Optional<User> optionalUser = userService.findByEmail(email);
        if (optionalUser.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "No existe una cuenta con ese correo electrónico.");
            return "redirect:/auth/forgot-password";
        }
        User user = optionalUser.get();
        if (user.getResetToken() == null || user.getResetTokenExpiry() == null ||
            !user.getResetToken().equals(resetToken) ||
            user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            redirectAttributes.addFlashAttribute("error", "Código inválido o expirado. Solicita uno nuevo.");
            redirectAttributes.addFlashAttribute("email", email);
            return "redirect:/auth/reset-password-token?email=" + email;
        }
        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Las contraseñas no coinciden.");
            redirectAttributes.addFlashAttribute("email", email);
            redirectAttributes.addFlashAttribute("resetToken", resetToken);
            return "redirect:/auth/change-password";
        }
        // Cambiar la contraseña y limpiar el token
        user.setPassword(userService.encodePassword(password));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userService.save(user);
        redirectAttributes.addFlashAttribute("success", "¡Contraseña cambiada exitosamente! Ya puedes iniciar sesión.");
        return "redirect:/auth/login";
    }

}
