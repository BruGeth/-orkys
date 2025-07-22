package com.pollerianorkys.restaurant.controller;

import com.pollerianorkys.restaurant.security.SessionTimeoutInterceptor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador REST para manejar información de sesión.
 * Proporciona endpoints para que el frontend pueda consultar
 * el estado de la sesión y tiempo restante.
 */
@RestController
@RequestMapping("/api/session")
public class SessionController {

    /**
     * Endpoint para obtener información de la sesión actual
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getSessionInfo(HttpSession session) {
        Map<String, Object> sessionInfo = new HashMap<>();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        boolean isAuthenticated = auth != null && auth.isAuthenticated() && 
                                !"anonymousUser".equals(auth.getName());
        
        sessionInfo.put("authenticated", isAuthenticated);
        
        if (isAuthenticated) {
            sessionInfo.put("username", auth.getName());
            sessionInfo.put("sessionId", session.getId());
            sessionInfo.put("remainingTime", SessionTimeoutInterceptor.getRemainingSessionTime(session));
            sessionInfo.put("maxInactiveInterval", session.getMaxInactiveInterval());
            
            // Información adicional de la sesión
            sessionInfo.put("creationTime", session.getCreationTime());
            sessionInfo.put("lastAccessedTime", session.getLastAccessedTime());
        } else {
            sessionInfo.put("remainingTime", 0);
        }
        
        return ResponseEntity.ok(sessionInfo);
    }

    /**
     * Endpoint para mantener la sesión activa (heartbeat)
     * Este endpoint se puede llamar periódicamente para resetear el timer de inactividad
     */
    @GetMapping("/heartbeat")
    public ResponseEntity<Map<String, Object>> sessionHeartbeat(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        boolean isAuthenticated = auth != null && auth.isAuthenticated() && 
                                !"anonymousUser".equals(auth.getName());
        
        if (isAuthenticated) {
            // Actualizar tiempo de última actividad
            session.setAttribute("lastActivityTime", System.currentTimeMillis());
            
            response.put("status", "success");
            response.put("message", "Sesión actualizada");
            response.put("remainingTime", SessionTimeoutInterceptor.getRemainingSessionTime(session));
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "error");
            response.put("message", "Usuario no autenticado");
            return ResponseEntity.status(401).body(response);
        }
    }

    /**
     * Endpoint para verificar si la sesión sigue válida
     */
    @GetMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateSession(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        boolean isAuthenticated = auth != null && auth.isAuthenticated() && 
                                !"anonymousUser".equals(auth.getName());
        
        if (isAuthenticated) {
            int remainingTime = SessionTimeoutInterceptor.getRemainingSessionTime(session);
            
            response.put("valid", remainingTime > 0);
            response.put("remainingTime", remainingTime);
            response.put("username", auth.getName());
            
            return ResponseEntity.ok(response);
        } else {
            response.put("valid", false);
            response.put("remainingTime", 0);
            return ResponseEntity.ok(response);
        }
    }
}
