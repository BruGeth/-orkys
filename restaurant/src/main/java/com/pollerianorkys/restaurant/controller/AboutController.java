package com.pollerianorkys.restaurant.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador para manejar las páginas de información sobre la empresa
 */
@Controller
@RequestMapping("/about")
public class AboutController {

    /**
     * Muestra la página "Nosotros"
     */
    @GetMapping("/us")
    public String showAboutUsPage(Model model) {
        return "nosotros";
    }

    /**
     * Muestra la página "Trabaja con nosotros"
     */
    @GetMapping("/work-with-us")
    public String showWorkWithUsPage(Model model) {
        return "trabaja-con-nosotros";
    }

    /**
     * Muestra la página de términos y condiciones de promociones
     */
    @GetMapping("/terms-promotions")
    public String showTermsPromotionsPage(Model model) {
        return "terminos-condiciones-promociones";
    }
}
