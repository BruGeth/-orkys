package com.pollerianorkys.restaurant.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpSession;

/**
 * Advisor global que añade información de sesión a todos los modelos de vista.
 * Proporciona datos de tiempo de sesión y estado de autenticación 
 * que pueden ser utilizados en las plantillas Thymeleaf.
 */
@ControllerAdvice
@Component
public class SecurityModelAdvice {

    /**
     * Añade información de sesión y seguridad a todos los modelos.
     * Esta información estará disponible en todas las vistas.
     */
    @ModelAttribute("sessionInfo")
    public SessionInfo addSessionInfo(HttpSession session) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        SessionInfo sessionInfo = new SessionInfo();
        sessionInfo.setAuthenticated(auth != null && auth.isAuthenticated() && 
                                   !"anonymousUser".equals(auth.getName()));
        
        if (sessionInfo.isAuthenticated()) {
            sessionInfo.setUsername(auth.getName());
            sessionInfo.setRemainingTime(SessionTimeoutInterceptor.getRemainingSessionTime(session));
            sessionInfo.setSessionId(session.getId());
        }
        
        return sessionInfo;
    }

    /**
     * Clase interna para encapsular información de sesión
     */
    public static class SessionInfo {
        private boolean authenticated;
        private String username;
        private int remainingTime;
        private String sessionId;

        // Getters y setters
        public boolean isAuthenticated() { return authenticated; }
        public void setAuthenticated(boolean authenticated) { this.authenticated = authenticated; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public int getRemainingTime() { return remainingTime; }
        public void setRemainingTime(int remainingTime) { this.remainingTime = remainingTime; }

        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    }
}
