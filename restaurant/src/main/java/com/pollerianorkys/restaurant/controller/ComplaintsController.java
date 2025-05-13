package com.pollerianorkys.restaurant.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

/**
 * Controlador para manejar el libro de reclamaciones
 */
@Controller
@RequestMapping("/complaints")
@RequiredArgsConstructor
public class ComplaintsController {

    /**
     * Muestra la página del libro de reclamaciones
     */
    @GetMapping("/book")
    public String showComplaintsBookPage(Model model) {
        // Aquí podrías agregar datos al modelo si es necesario
        return "complaintsBook";
    }
    
    /**
     * Procesa el envío de una reclamación
     */
    @PostMapping("/submit")
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
        
        return "redirect:/complaints/book";
    }
}
