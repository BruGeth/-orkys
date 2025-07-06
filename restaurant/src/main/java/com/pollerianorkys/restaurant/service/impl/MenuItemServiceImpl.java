package com.pollerianorkys.restaurant.service.impl;

import com.pollerianorkys.restaurant.model.MenuItem;
import com.pollerianorkys.restaurant.repository.MenuItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MenuItemServiceImpl {

    private final MenuItemRepository menuItemRepository;

    @Autowired
    public MenuItemServiceImpl(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    /**
     * Obtiene todos los platillos
     */
    public List<MenuItem> getAll() {
        return menuItemRepository.findAll();
    }

    /**
     * Busca un platillo por ID
     */
    public MenuItem findById(Long id) {
        Optional<MenuItem> menuItem = menuItemRepository.findById(id);
        return menuItem.orElse(null);
    }

    /**
     * Guarda o actualiza un platillo
     */
    public MenuItem save(MenuItem menuItem) {
        return menuItemRepository.save(menuItem);
    }

    /**
     * Elimina un platillo por ID
     */
    public void delete(Long id) {
        menuItemRepository.deleteById(id);
    }

    /**
     * Verifica si existe un platillo por ID
     */
    public boolean existsById(Long id) {
        return menuItemRepository.existsById(id);
    }

    /**
     * Cuenta el total de platillos
     */
    public long count() {
        return menuItemRepository.count();
    }
}