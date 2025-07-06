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

    public List<MenuItem> getAll() {
        return menuItemRepository.findAll();
    }

    public MenuItem findById(Long id) {
        Optional<MenuItem> menuItem = menuItemRepository.findById(id);
        return menuItem.orElse(null);
    }

    public MenuItem save(MenuItem menuItem) {
        return menuItemRepository.save(menuItem);
    }

    public void delete(Long id) {
        menuItemRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return menuItemRepository.existsById(id);
    }

}