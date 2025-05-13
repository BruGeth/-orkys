package com.pollerianorkys.restaurant.controller;

import com.pollerianorkys.restaurant.dto.UserRegistrationDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    // Página principal
    @GetMapping("/")
    public String showIndex() {
        return "home";
    }

    // Página de login
    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }
    
    // Muestra el formulario de registro
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "register";
    }

    @GetMapping("/libro-reclamaciones")
    public String complaintsBookPage() {
        return "complaintsBook";
    }

}
