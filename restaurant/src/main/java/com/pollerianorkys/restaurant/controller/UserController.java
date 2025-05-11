package com.pollerianorkys.restaurant.controller;

import com.pollerianorkys.restaurant.dto.UserRegistrationDto;
import com.pollerianorkys.restaurant.model.User;
import com.pollerianorkys.restaurant.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

@GetMapping("/register")
public String showRegistrationForm(Model model) {
    model.addAttribute("user", new UserRegistrationDto());
    return "auth/register";
}

@PostMapping("/register")
public String processRegister(@ModelAttribute("user") UserRegistrationDto userDto, Model model, HttpServletRequest request) {
    User savedUser = userService.registerUser(userDto);

    // TODO: Lógica para enviar correo de verificación aquí

    model.addAttribute("message", "Registro exitoso. Revisa tu correo para verificar la cuenta.");
    return "auth/register_success";
}

} 