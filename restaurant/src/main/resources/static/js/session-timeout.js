/**
 * Sistema de Timeout de Sesión
 * Timeout: 1 minuto total
 * Advertencia: 15 segundos antes (a los 45 segundos)
 * Basado en el sistema que funcionaba bien, solo con tiempos más cortos
 */

class SessionTimeoutManager {
    constructor() {
        // Configuración de tiempos (en milisegundos)
        this.SESSION_TIMEOUT = 1 * 60 * 1000; // 1 minuto total
        this.WARNING_TIME = 15 * 1000; // 15 segundos de advertencia
        this.WARNING_STARTS_AT = this.SESSION_TIMEOUT - this.WARNING_TIME; // 45 segundos
        
        // Estados del manager
        this.warningShown = false;
        this.sessionTimer = null;
        this.warningTimer = null;
        this.countdownTimer = null;
        
        // Eventos que resetean el timeout
        this.activityEvents = ['mousedown', 'mousemove', 'keypress', 'scroll', 'touchstart', 'click'];
        
        this.init();
    }
    
    init() {
        console.log('🔐 Sistema de timeout de sesión iniciado - 1 minuto total');
        console.log('⏰ Advertencia aparecerá a los 45 segundos');
        
        this.createWarningModal();
        this.bindActivityEvents();
        this.startSessionTimer();
    }
    
    bindActivityEvents() {
        this.activityEvents.forEach(eventType => {
            document.addEventListener(eventType, () => this.handleUserActivity(), true);
        });
        
        // Detectar cambios de pestaña como actividad
        document.addEventListener('visibilitychange', () => {
            if (!document.hidden) {
                this.handleUserActivity();
            }
        });
    }
    
    handleUserActivity() {
        // Solo resetear si no estamos en proceso de logout Y si no hay modal de advertencia visible
        if (!this.loggingOut && !this.warningShown) {
            this.resetSessionTimer();
        }
    }
    
    startSessionTimer() {
        this.clearAllTimers();
        
        console.log('⏰ Iniciando timeout de sesión - 1 minuto');
        
        // Timer para mostrar advertencia (45 segundos)
        this.warningTimer = setTimeout(() => {
            this.showWarning();
        }, this.WARNING_STARTS_AT);
        
        // Timer para logout automático (1 minuto)
        this.sessionTimer = setTimeout(() => {
            this.performLogout();
        }, this.SESSION_TIMEOUT);
    }
    
    resetSessionTimer() {
        // Solo iniciar nuevo timer, NO ocultar advertencia automáticamente
        this.startSessionTimer();
    }
    
    resetSessionTimerAndHideWarning() {
        // Método específico para cuando el usuario explícitamente quiere continuar
        if (this.warningShown) {
            console.log('🔄 Usuario eligió continuar - Ocultando advertencia y reseteando timer');
            this.hideWarning();
        }
        
        this.startSessionTimer();
    }
    
    showWarning() {
        if (this.warningShown || this.loggingOut) return;
        
        console.log('⚠️ Mostrando advertencia de timeout');
        this.warningShown = true;
        
        const modal = document.getElementById('sessionTimeoutModal');
        if (modal) {
            const bootstrapModal = new bootstrap.Modal(modal);
            bootstrapModal.show();
            
            this.startCountdown();
        }
    }
    
    hideWarning() {
        if (!this.warningShown) return;
        
        console.log('✅ Ocultando advertencia de timeout');
        this.warningShown = false;
        
        const modal = document.getElementById('sessionTimeoutModal');
        if (modal) {
            const bootstrapModal = bootstrap.Modal.getInstance(modal);
            if (bootstrapModal) {
                bootstrapModal.hide();
            }
        }
        
        if (this.countdownTimer) {
            clearInterval(this.countdownTimer);
            this.countdownTimer = null;
        }
    }
    
    startCountdown() {
        let timeLeft = 15; // 15 segundos de countdown
        const countdownElement = document.getElementById('countdownDisplay');
        
        if (countdownElement) {
            countdownElement.textContent = timeLeft;
        }
        
        this.countdownTimer = setInterval(() => {
            timeLeft--;
            if (countdownElement) {
                countdownElement.textContent = timeLeft;
                
                // Cambiar color según tiempo restante
                if (timeLeft <= 5) {
                    countdownElement.className = 'fw-bold text-danger';
                } else if (timeLeft <= 10) {
                    countdownElement.className = 'fw-bold text-warning';
                } else {
                    countdownElement.className = 'fw-bold text-info';
                }
            }
            
            if (timeLeft <= 0) {
                clearInterval(this.countdownTimer);
                this.countdownTimer = null;
            }
        }, 1000);
    }
    
    performLogout() {
        if (this.loggingOut) return;
        
        console.log('⏰ Timeout de sesión - Redirigiendo al login');
        this.loggingOut = true;
        
        this.clearAllTimers();
        this.hideWarning();
        
        // Mostrar mensaje de logout
        this.showLogoutMessage();
        
        // Redirigir después de un breve delay
        setTimeout(() => {
            window.location.href = '/auth/login?timeout=true';
        }, 2000);
    }
    
    showLogoutMessage() {
        // Crear overlay de logout
        const overlay = document.createElement('div');
        overlay.id = 'logoutOverlay';
        overlay.style.cssText = `
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0, 0, 0, 0.8);
            display: flex;
            justify-content: center;
            align-items: center;
            z-index: 9999;
            color: white;
            font-family: Arial, sans-serif;
        `;
        
        overlay.innerHTML = `
            <div class="text-center">
                <div class="spinner-border text-light mb-3" role="status">
                    <span class="visually-hidden">Cargando...</span>
                </div>
                <h4>Sesión Expirada</h4>
                <p>Su sesión ha expirado por inactividad (1 minuto).</p>
                <p>Redirigiendo al login...</p>
            </div>
        `;
        
        document.body.appendChild(overlay);
    }
    
    extendSession() {
        console.log('✅ Usuario solicitó extender sesión');
        this.resetSessionTimerAndHideWarning();
    }
    
    logoutNow() {
        console.log('🚪 Usuario solicitó cerrar sesión inmediatamente');
        this.performLogout();
    }
    
    clearAllTimers() {
        if (this.sessionTimer) {
            clearTimeout(this.sessionTimer);
            this.sessionTimer = null;
        }
        
        if (this.warningTimer) {
            clearTimeout(this.warningTimer);
            this.warningTimer = null;
        }
        
        if (this.countdownTimer) {
            clearInterval(this.countdownTimer);
            this.countdownTimer = null;
        }
    }
    
    createWarningModal() {
        // Verificar si ya existe
        if (document.getElementById('sessionTimeoutModal')) {
            return;
        }
        
        const modalHtml = `
            <div class="modal fade" id="sessionTimeoutModal" tabindex="-1" role="dialog" 
                 aria-labelledby="sessionTimeoutModalLabel" aria-hidden="true" data-bs-backdrop="static" data-bs-keyboard="false">
                <div class="modal-dialog modal-dialog-centered" role="document">
                    <div class="modal-content border-warning">
                        <div class="modal-header bg-warning text-dark">
                            <h5 class="modal-title" id="sessionTimeoutModalLabel">
                                <i class="fas fa-exclamation-triangle me-2"></i>
                                Sesión por Expirar
                            </h5>
                        </div>
                        <div class="modal-body text-center">
                            <div class="mb-3">
                                <i class="fas fa-clock fa-3x text-warning mb-3"></i>
                            </div>
                            <h6 class="mb-3">Tu sesión expirará por inactividad</h6>
                            <p class="mb-3">
                                Su sesión se cerrará automáticamente en:
                            </p>
                            <div class="alert alert-warning">
                                <h4 class="mb-0">
                                    <span id="countdownDisplay" class="fw-bold text-info">15</span> segundos
                                </h4>
                            </div>
                            <p class="text-muted small">
                                Haga clic en "Continuar Sesión" para seguir trabajando
                            </p>
                        </div>
                        <div class="modal-footer justify-content-center">
                            <button type="button" class="btn btn-success" id="extendSessionBtn">
                                <i class="fas fa-check me-2"></i>
                                Continuar Sesión
                            </button>
                            <button type="button" class="btn btn-outline-secondary" id="logoutNowBtn">
                                <i class="fas fa-sign-out-alt me-2"></i>
                                Cerrar Sesión Ahora
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        `;
        
        document.body.insertAdjacentHTML('beforeend', modalHtml);
        
        // Bind eventos de los botones
        document.getElementById('extendSessionBtn').addEventListener('click', () => {
            this.extendSession();
        });
        
        document.getElementById('logoutNowBtn').addEventListener('click', () => {
            this.logoutNow();
        });
    }
    
    destroy() {
        console.log('🔒 Destruyendo SessionTimeoutManager');
        this.clearAllTimers();
        
        // Remover eventos
        this.activityEvents.forEach(eventType => {
            document.removeEventListener(eventType, () => this.handleUserActivity(), true);
        });
        
        // Remover modal
        const modal = document.getElementById('sessionTimeoutModal');
        if (modal) {
            modal.remove();
        }
    }
}

// Auto-inicialización cuando el DOM esté listo
document.addEventListener('DOMContentLoaded', () => {
    // Solo inicializar si el usuario está autenticado
    if (window.sessionInfo && window.sessionInfo.authenticated) {
        window.sessionTimeoutManager = new SessionTimeoutManager();
        console.log('✅ Sistema de timeout iniciado - 1 minuto (45s normales + 15s advertencia)');
    } else {
        console.log('ℹ️ Usuario no autenticado - Sistema de timeout no iniciado');
    }
});

// Limpiar al salir de la página
window.addEventListener('beforeunload', () => {
    if (window.sessionTimeoutManager) {
        window.sessionTimeoutManager.destroy();
    }
});
