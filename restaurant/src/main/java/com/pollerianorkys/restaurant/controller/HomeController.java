package com.pollerianorkys.restaurant.controller;

import com.pollerianorkys.restaurant.repository.MenuItemRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador para manejar las páginas principales de la aplicación
 */
@Controller
public class HomeController {

    private final MenuItemRepository menuItemRepository;

    public HomeController(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

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
    public String showMenuPage(Model model) {
        model.addAttribute("platillos", menuItemRepository.findAll());
        return "carta";
    }

    /**
     * Muestra la página de promociones
     */
    @GetMapping("/promociones")
    public String showPromotionsPage() {
        return "redirect:/promotions";
    }

    /**
     * Muestra la página de locales
     */
    @GetMapping("/locales")
    public String showLocationsPage() {
        return "locales";
    }

    /**
     * Muestra la página de nosotros
     */
    @GetMapping("/nosotros")
    public String showAboutUsPage() {
        return "redirect:/about/us";
    }

    /**
     * Muestra la página de trabaja con nosotros
     */
    @GetMapping("/trabaja-con-nosotros")
    public String showWorkWithUsPage() {
        return "redirect:/about/work-with-us";
    }

    /**
     * Muestra la página de términos y condiciones de promociones
     */
    @GetMapping("/terminos")
    public String showTermsPromotionsPage() {
        return "redirect:/about/terms-promotions";
    }
}
