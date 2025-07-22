package com.pollerianorkys.restaurant.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor para manejar el timeout de sesión y actividad del usuario.
 * Este interceptor monitorea cada request del usuario para mantener 
 * actualizada la última actividad y controlar el timeout de sesión.
 */
@Component
public class SessionTimeoutInterceptor implements HandlerInterceptor {

    // Timeout de sesión en segundos (1 minuto = 60 segundos)
    private static final int SESSION_TIMEOUT = 60;
    
    // Nombre del atributo que almacena la última actividad
    private static final String LAST_ACTIVITY_TIME = "lastActivityTime";

    /**
     * Se ejecuta antes de procesar cada request.
     * Verifica si la sesión ha expirado por inactividad y actualiza 
     * la última actividad si el usuario está autenticado.
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Solo aplicar timeout si el usuario está autenticado y tiene sesión
        if (session != null && authentication != null && authentication.isAuthenticated() 
            && !"anonymousUser".equals(authentication.getName())) {
            
            Long lastActivityTime = (Long) session.getAttribute(LAST_ACTIVITY_TIME);
            long currentTime = System.currentTimeMillis();

            if (lastActivityTime != null) {
                // Calcular tiempo transcurrido desde la última actividad
                long timeSinceLastActivity = (currentTime - lastActivityTime) / 1000;

                // Si ha pasado más tiempo del permitido, invalidar sesión
                if (timeSinceLastActivity > SESSION_TIMEOUT) {
                    session.invalidate();
                    SecurityContextHolder.clearContext();
                    
                    // Redirigir al login con mensaje de timeout
                    response.sendRedirect(request.getContextPath() + "/auth/login?timeout=true");
                    return false;
                }
            }

            // Actualizar tiempo de última actividad
            session.setAttribute(LAST_ACTIVITY_TIME, currentTime);
            // Configurar timeout máximo de sesión HTTP
            session.setMaxInactiveInterval(SESSION_TIMEOUT);
        }

        return true;
    }

    /**
     * Obtiene el tiempo restante de sesión en segundos
     */
    public static int getRemainingSessionTime(HttpSession session) {
        if (session == null) return 0;
        
        Long lastActivityTime = (Long) session.getAttribute(LAST_ACTIVITY_TIME);
        if (lastActivityTime == null) return SESSION_TIMEOUT;
        
        long timeSinceLastActivity = (System.currentTimeMillis() - lastActivityTime) / 1000;
        return Math.max(0, SESSION_TIMEOUT - (int) timeSinceLastActivity);
    }
}
