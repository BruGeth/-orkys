package com.pollerianorkys.restaurant.controller;

import com.pollerianorkys.restaurant.dto.UserRegistrationDto;
import com.pollerianorkys.restaurant.service.UserService;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    
    // Procesa el formulario de registro
    @PostMapping("/register")
    public String processRegister(@Valid @ModelAttribute("user") UserRegistrationDto userDto,
                                  BindingResult result,
                                  Model model) {

        if (result.hasErrors()) {
            return "register";
        }

        userService.registerUser(userDto);
        return "register_success";
    }
    
    
    
    @PostMapping("/libro-reclamaciones/submit")
    public String processComplaint(@RequestParam Map<String, String> formData, 
                                  RedirectAttributes redirectAttributes) {
        // Aquí implementarías la lógica para guardar la reclamación
        // Por ahora, solo simulamos un procesamiento exitoso
        
        // Ejemplo de procesamiento básico
        System.out.println("Reclamación recibida de: " + formData.get("nombreCompleto"));
        System.out.println("Tipo: " + formData.get("tipoReclamo"));
        System.out.println("Detalle: " + formData.get("detalleReclamo"));
        
        // Añadir mensaje de éxito
        redirectAttributes.addFlashAttribute("success", "¡Reclamación enviada con éxito!");
        
        return "redirect:/libro-reclamaciones";
    }
    
    
}
