package com.pollerianorkys.restaurant.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador para manejar las páginas principales de la aplicación
 */
@Controller
public class HomeController {

    /**
     * Redirecciona la raíz a la página de inicio
     */
    @GetMapping("/")
    public String redirectToHome() {
        return "redirect:/home";
    }

    /**
     * Muestra la página de inicio
     */
    @GetMapping("/home")
    public String showHomePage(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            model.addAttribute("username", authentication.getName());
        }
        return "home";
    }
    
    /**
     * Muestra la página de carta
     */
    @GetMapping("/carta")
    public String showMenuPage() {
        return "carta";
    }
    
    /**
     * Muestra la página de promociones
     */
    @GetMapping("/promociones")
    public String showPromotionsPage() {
        return "promociones";
    }
    
    /**
     * Muestra la página de locales
     */
    @GetMapping("/locales")
    public String showLocationsPage() {
        return "locales";
    }
}
