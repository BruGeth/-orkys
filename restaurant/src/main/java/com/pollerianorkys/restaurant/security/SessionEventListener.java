package com.pollerianorkys.restaurant.security;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listener para eventos de sesión HTTP.
 * Se encarga de monitorear cuando se crean y destruyen sesiones,
 * proporcionando logs de seguridad. SOLO maneja timeout de 1 minuto.
 */
public class SessionEventListener implements HttpSessionListener {

    private static final Logger logger = LoggerFactory.getLogger(SessionEventListener.class);
    private final AtomicInteger sessionCounter = new AtomicInteger(0);

    public SessionEventListener() {
        // Constructor simple sin dependencias
    }

    /**
     * Se ejecuta cuando se crea una nueva sesión HTTP.
     * Configura timeout de 1 minuto y registra el evento.
     */
    @Override
    public void sessionCreated(HttpSessionEvent se) {
        String sessionId = se.getSession().getId();
        logger.info("🆕 Nueva sesión creada: " + sessionId);
        
        // Configurar timeout de 1 minuto (60 segundos)
        se.getSession().setMaxInactiveInterval(60);
        
        // Debug: Mostrar total de sesiones HTTP
        logger.info("📊 Total de sesiones HTTP activas: " + sessionCounter.incrementAndGet());
        logger.info("⏰ Timeout configurado: 1 minuto (45s normales + 15s con aviso)");
    }

    /**
     * Se ejecuta cuando una sesión HTTP es destruida.
     * Solo registra el evento - NO hace limpieza del SessionRegistry.
     */
    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        String sessionId = se.getSession().getId();
        logger.info("❌ Sesión destruida: " + sessionId);
        
        // Solo logging - NO limpieza del SessionRegistry
        logger.info("📊 Total de sesiones HTTP restantes: " + sessionCounter.decrementAndGet());
    }
}
