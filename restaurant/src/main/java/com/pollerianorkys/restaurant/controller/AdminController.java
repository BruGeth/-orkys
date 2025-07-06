package com.pollerianorkys.restaurant.controller;

import com.pollerianorkys.restaurant.model.MenuItem;
import com.pollerianorkys.restaurant.service.impl.MenuItemServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final MenuItemServiceImpl menuItemService;

    @Autowired
    public AdminController(MenuItemServiceImpl menuItemService) {
        this.menuItemService = menuItemService;
    }

    /**
     * Muestra la página de edición del menú con todos los platillos
     */
    @GetMapping("/edit")
    public String editMenu(Model model) {
        model.addAttribute("menuItem", new MenuItem());
        model.addAttribute("menu", menuItemService.getAll());
        model.addAttribute("editMode", false);
        return "editar-menu";
    }

    /**
     * Guarda un nuevo platillo o actualiza uno existente
     */
    @PostMapping("/save")
    public String saveMenuItem(@Valid @ModelAttribute MenuItem menuItem,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes,
                               Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("menu", menuItemService.getAll());
            model.addAttribute("editMode", menuItem.getId() != null);
            model.addAttribute("error", "Por favor corrige los errores en el formulario");
            return "editar-menu";
        }

        try {
            boolean isNew = menuItem.getId() == null;
            menuItemService.save(menuItem);

            if (isNew) {
                redirectAttributes.addFlashAttribute("success",
                        "Platillo '" + menuItem.getNombre() + "' agregado exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("success",
                        "Platillo '" + menuItem.getNombre() + "' actualizado exitosamente");
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error al guardar el platillo: " + e.getMessage());
        }

        return "redirect:/admin/edit";
    }

    /**
     * Muestra el formulario para editar un platillo específico
     */
    @GetMapping("/edit/{id}")
    public String editMenuItem(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            MenuItem menuItem = menuItemService.findById(id);

            if (menuItem != null) {
                model.addAttribute("menuItem", menuItem);
                model.addAttribute("menu", menuItemService.getAll());
                model.addAttribute("editMode", true);
                model.addAttribute("editingItemId", id);
                return "editar-menu";
            } else {
                redirectAttributes.addFlashAttribute("error",
                        "No se encontró el platillo con ID: " + id);
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error al cargar el platillo: " + e.getMessage());
        }

        return "redirect:/admin/edit";
    }

    /**
     * Elimina un platillo por su ID
     */
    @PostMapping("/delete/{id}")
    public String deleteMenuItem(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            MenuItem menuItem = menuItemService.findById(id);

            if (menuItem != null) {
                menuItemService.delete(id);
                redirectAttributes.addFlashAttribute("success",
                        "Platillo '" + menuItem.getNombre() + "' eliminado exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("error",
                        "No se encontró el platillo con ID: " + id);
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error al eliminar el platillo: " + e.getMessage());
        }

        return "redirect:/admin/edit";
    }
}