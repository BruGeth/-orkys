package com.pollerianorkys.restaurant.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador para manejar las páginas de promociones
 */
@Controller
@RequestMapping("/promotions")
public class PromotionsController {

    /**
     * Muestra la página de promociones
     */
    @GetMapping
    public String showPromotionsPage(Model model) {
        return "promociones";
    }
}
