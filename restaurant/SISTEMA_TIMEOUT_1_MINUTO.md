# 🔐 Sistema de Timeout de Sesión - 1 Minuto
**Documentación del Sistema de Seguridad por Inactividad - Ñorkys Restaurant**

---

## 📋 **Resumen del Sistema**

Sistema de seguridad que **cierra automáticamente la sesión después de 1 minuto de inactividad**, con modal de advertencia a los 45 segundos.

### ⏰ **Tiempos Configurados:**
- **45 segundos**: Actividad normal sin interrupciones
- **15 segundos**: Modal de advertencia con countdown
- **1 minuto total**: Cierre automático de sesión

---

## 🏗️ **Arquitectura del Sistema**

### **Backend (Java/Spring Boot)**

#### **1. SessionTimeoutInterceptor.java**
**Ubicación:** `src/main/java/com/pollerianorkys/restaurant/security/`

```java
// Timeout de sesión en segundos (1 minuto = 60 segundos)
private static final int SESSION_TIMEOUT = 60;
```

**Función:**
- Intercepta **todas las peticiones HTTP** de usuarios autenticados
- Verifica si han pasado más de 60 segundos desde la última actividad
- Actualiza automáticamente el timestamp de actividad
- Invalida la sesión y redirige a login si excede el timeout

**¿Cómo funciona?**
1. Usuario hace una petición → `preHandle()` se ejecuta
2. Se verifica tiempo transcurrido desde última actividad
3. Si > 60 segundos → `session.invalidate()` + redirect a `/auth/login?timeout=true`
4. Si ≤ 60 segundos → Se actualiza `lastActivityTime` y continúa

#### **2. SessionEventListener.java**
**Ubicación:** `src/main/java/com/pollerianorkys/restaurant/security/`

```java
// Configurar timeout de 1 minuto (60 segundos)
se.getSession().setMaxInactiveInterval(60);
```

**Función:**
- Escucha eventos de creación y destrucción de sesiones HTTP
- Configura el timeout básico de 60 segundos al crear cada sesión
- Proporciona logs de auditoría de seguridad

**¿Cuándo se ejecuta?**
- `sessionCreated()`: Al crear nueva sesión (login, primera visita)
- `sessionDestroyed()`: Al destruir sesión (timeout, logout manual)

#### **3. SecurityConfig.java**
**Configuración simplificada sin control de sesiones concurrentes:**

```java
.sessionManagement(session -> session
    .sessionAuthenticationStrategy(sessionAuthenticationStrategy())
    .invalidSessionUrl("/auth/login?expired=true")
)
```

**¿Qué hace?**
- **NO limita** número de sesiones por usuario (múltiples ventanas permitidas)
- **SOLO maneja** timeout de inactividad
- Redirige a login con parámetros apropiados

#### **4. AuthController.java**
**Manejo de mensajes específicos:**

```java
if (timeout != null) {
    model.addAttribute("timeoutMessage", 
        "Tu sesión ha expirado por inactividad (1 minuto).");
}
```

---

### **Frontend (JavaScript)**

#### **session-timeout.js**
**Ubicación:** `src/main/resources/static/js/`

```javascript
this.SESSION_TIMEOUT = 1 * 60 * 1000; // 1 minuto total
this.WARNING_TIME = 15 * 1000;        // 15 segundos de advertencia
this.WARNING_STARTS_AT = 45 * 1000;   // Advertencia a los 45 segundos
```

**Función:**
- Monitorea actividad del usuario (mouse, teclado, scroll, clicks)
- Resetea automáticamente el timer con cualquier actividad
- Muestra modal de advertencia a los 45 segundos
- Redirige automáticamente al login a 1 minuto

**Eventos monitoreados:**
- `mousedown`, `mousemove`, `keypress`, `scroll`, `touchstart`, `click`
- `visibilitychange` (cambio de pestaña)

---

## 🔄 **Flujo Completo de Funcionamiento**

### **1. Usuario Inicia Sesión**
1. Spring Security autentica al usuario
2. `SessionEventListener.sessionCreated()` se ejecuta
3. Se configura timeout de 60 segundos: `setMaxInactiveInterval(60)`
4. JavaScript `SessionTimeoutManager` se inicializa
5. **Timer de 1 minuto comienza**

### **2. Durante la Navegación (0-45 segundos)**
1. Usuario navega, hace clicks, mueve mouse
2. `SessionTimeoutInterceptor.preHandle()` intercepta cada petición
3. Se actualiza `lastActivityTime` en la sesión
4. JavaScript detecta actividad → `resetSessionTimer()`
5. **Sistema funciona transparentemente**

### **3. Advertencia (45 segundos)**
1. JavaScript detecta 45 segundos de inactividad
2. Se muestra modal Bootstrap con countdown de 15 segundos
3. Usuario puede hacer click en "Continuar Sesión" → resetea timer
4. Si no hay actividad, countdown continúa hacia 0

### **4. Timeout Automático (60 segundos)**

**Lado Servidor:**
1. `SessionTimeoutInterceptor` detecta > 60 segundos
2. `session.invalidate()` + `SecurityContextHolder.clearContext()`
3. Redirect a `/auth/login?timeout=true`

**Lado Cliente:**
1. JavaScript ejecuta timeout a 60 segundos
2. Muestra mensaje "Sesión Expirada"
3. Redirect a `/auth/login?timeout=true`

---

## ⚙️ **Configuración**

### **Cambiar Timeout:**

**Servidor (Java):**
```java
// En SessionTimeoutInterceptor.java
private static final int SESSION_TIMEOUT = 60; // Cambiar aquí

// En SessionEventListener.java
se.getSession().setMaxInactiveInterval(60); // Y aquí
```

**Cliente (JavaScript):**
```javascript
// En session-timeout.js
this.SESSION_TIMEOUT = 1 * 60 * 1000; // 1 minuto en milisegundos
this.WARNING_TIME = 15 * 1000;        // Tiempo de advertencia
```

### **Páginas Excluidas del Timeout:**
```java
// En WebConfig.java - No se aplica timeout a:
"/css/**", "/js/**", "/img/**"     // Recursos estáticos
"/auth/login", "/auth/register"    // Páginas de autenticación  
"/", "/home", "/carta"             // Páginas públicas
```

---

## 🎯 **Características de Seguridad**

### ✅ **Lo que SÍ hace el sistema:**
- **Timeout por inactividad:** 1 minuto exacto
- **Advertencia previa:** Modal a los 45 segundos
- **Detección de actividad:** Cualquier interacción resetea el timer
- **Logs de auditoría:** Registra creación/destrucción de sesiones
- **Múltiples ventanas:** Permitidas (sin restricciones)

### ❌ **Lo que NO hace el sistema:**
- **Control de sesiones concurrentes:** Eliminado completamente
- **Límite de sesiones por usuario:** Sin restricciones
- **Invalidación entre dispositivos:** No existe esta funcionalidad

---

## 🧪 **Cómo Probar el Sistema**

### **Prueba 1: Timeout Normal**
1. Inicia sesión en la aplicación
2. Navega normalmente por 40 segundos
3. **Resultado:** No pasa nada, sistema transparente

### **Prueba 2: Advertencia**
1. Deja la aplicación inactiva por 45 segundos
2. **Resultado:** Aparece modal con countdown de 15 segundos
3. Haz click en "Continuar Sesión"
4. **Resultado:** Modal desaparece, timer se resetea

### **Prueba 3: Timeout Completo**
1. Deja la aplicación inactiva por 1 minuto completo
2. **Resultado:** Redirección automática a login
3. **Mensaje:** "Tu sesión ha expirado por inactividad (1 minuto)"

### **Prueba 4: Actividad Continua**
1. Mueve el mouse o navega constantemente
2. **Resultado:** Nunca aparece el modal, sesión permanece activa

---

## 🛠️ **Debugging y Monitoreo**

### **Logs del Servidor:**
```java
🆕 Nueva sesión creada: [SESSION_ID]
📊 Total de sesiones HTTP activas: 1
⏰ Timeout configurado: 1 minuto (45s normales + 15s con aviso)
❌ Sesión destruida: [SESSION_ID]
```

### **Logs del Cliente (Consola F12):**
```javascript
🔐 Sistema de timeout de sesión iniciado - 1 minuto total
⏰ Advertencia aparecerá a los 45 segundos
⚠️ Mostrando advertencia de timeout
🔄 Actividad detectada - Ocultando advertencia y reseteando timer
⏰ Timeout de sesión - Redirigiendo al login
```

### **Información de Sesión:**
```javascript
// En consola del navegador:
console.log(window.sessionInfo);
// Muestra: authenticated, username, remainingTime, sessionId
```

---

## 📋 **Archivos del Sistema**

```
src/main/java/com/pollerianorkys/restaurant/
├── security/
│   ├── SessionTimeoutInterceptor.java    ← Interceptor principal (60s)
│   ├── SessionEventListener.java         ← Listener de eventos HTTP
│   └── SecurityModelAdvice.java          ← Datos para templates
├── config/
│   ├── SecurityConfig.java               ← Configuración Spring Security
│   └── WebConfig.java                    ← Registro del interceptor
└── controller/
    └── AuthController.java               ← Mensajes de login

src/main/resources/
├── static/js/
│   └── session-timeout.js                ← Cliente JavaScript (1 min)
└── templates/
    ├── login.html                        ← Mensajes de timeout
    └── fragments/head.html               ← Inclusión del script
```

---

## 🚀 **Ventajas del Sistema Actual**

1. **Simple y Confiable:** Solo timeout, sin complejidades innecesarias
2. **Experiencia de Usuario:** Advertencia clara antes del cierre
3. **Múltiples Ventanas:** Funciona bien con pestañas múltiples
4. **Responsive:** Detecta cualquier tipo de actividad del usuario
5. **Auditable:** Logs completos de actividad de sesiones
6. **Mantenible:** Configuración centralizada y fácil de modificar

---

## ⚡ **Cambios Recientes**

- ✅ **Tiempo reducido:** De 5 minutos a 1 minuto
- ✅ **Lógica simplificada:** Eliminado control de sesiones concurrentes
- ✅ **Mensajes actualizados:** Reflejan el nuevo timeout de 1 minuto
- ✅ **Comentarios corregidos:** Documentación coherente con la funcionalidad actual

---

**¡Sistema probado y funcionando correctamente en http://localhost:8081! 🎉**
