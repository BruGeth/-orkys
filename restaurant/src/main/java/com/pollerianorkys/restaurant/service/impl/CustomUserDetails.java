package com.pollerianorkys.restaurant.service.impl;

import com.pollerianorkys.restaurant.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {
    private final User usuario;

    public CustomUserDetails(User usuario) {
        this.usuario = usuario;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of((GrantedAuthority) usuario.getAuthority()) ;
    }

    @Override
    public String getPassword() {
        return usuario.getPassword();
    }

    @Override
    public String getUsername() {
        return usuario.getUsername();
    }

    @Override
    public boolean isEnabled() { return usuario.isVerified(); }

    public String getCorreo(){
        return usuario.getEmail();
    }

    public String getCelular(){
        return usuario.getPhone();
    }
}
