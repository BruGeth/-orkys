package com.pollerianorkys.restaurant.controller;

import com.pollerianorkys.restaurant.dto.PasswordChangeDto;
import com.pollerianorkys.restaurant.dto.UserProfileDto;
import com.pollerianorkys.restaurant.model.User;
import com.pollerianorkys.restaurant.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

import java.util.Optional;

/**
 * Controlador para manejar operaciones relacionadas con el usuario
 */
@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Muestra la página de perfil del usuario
     */
    @GetMapping("/profile")
    public String showProfilePage(Model model) {
        // Obtener el usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        Optional<User> userOpt = userService.findByUsername(username);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            // Crear DTO para el perfil
            UserProfileDto profileDto = new UserProfileDto();
            profileDto.setFirstName(user.getFirstName());
            profileDto.setLastName(user.getLastName());
            profileDto.setEmail(user.getEmail());
            profileDto.setPhone(user.getPhone());
            
            model.addAttribute("userProfile", profileDto);
            model.addAttribute("passwordChange", new PasswordChangeDto());
        }
        
        return "profile";
    }
    
    /**
     * Procesa la actualización del perfil del usuario
     */
    @PostMapping("/profile/update")
    public String updateProfile(@Valid @ModelAttribute("userProfile") UserProfileDto profileDto,
                               BindingResult result,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "profile";
        }
        
        try {
            // Obtener el usuario autenticado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            
            // Actualizar perfil
            userService.updateProfile(username, profileDto);
            
            redirectAttributes.addFlashAttribute("success", "Perfil actualizado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el perfil: " + e.getMessage());
        }
        
        return "redirect:/user/profile";
    }
    
    /**
     * Procesa el cambio de contraseña del usuario
     */
    @PostMapping("/profile/change-password")
    public String changePassword(@Valid @ModelAttribute("passwordChange") PasswordChangeDto passwordDto,
                                BindingResult result,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "profile";
        }
        
        // Verificar que las contraseñas coincidan
        if (!passwordDto.getNewPassword().equals(passwordDto.getConfirmPassword())) {
            redirectAttributes.addFlashAttribute("error", "Las contraseñas no coinciden");
            return "redirect:/user/profile";
        }
        
        try {
            // Obtener el usuario autenticado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            
            // Cambiar contraseña
            boolean changed = userService.changePassword(username, 
                                                        passwordDto.getCurrentPassword(), 
                                                        passwordDto.getNewPassword());
            
            if (changed) {
                redirectAttributes.addFlashAttribute("success", "Contraseña actualizada correctamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "La contraseña actual es incorrecta");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cambiar la contraseña: " + e.getMessage());
        }
        
        return "redirect:/user/profile";
    }
    
    /**
     * Muestra la página de pedidos del usuario
     */
    @GetMapping("/orders")
    public String showOrdersPage() {
        return "orders";
    }
}
