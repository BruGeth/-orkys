package com.pollerianorkys.restaurant.service.impl;

import com.pollerianorkys.restaurant.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendVerificationEmail(String toEmail, String token) {
        // Verifica que el correo electrónico y el token no sean nulos o vacíos
        String subject = "Verifica tu cuenta - Pollería Norkys";
        String content = """
            <div style="font-family: sans-serif;">
                <h2 style="color: #198754;">¡Bienvenido!</h2>
                <p>Tu código de verificación es:</p>
                <div style="font-size: 24px; font-weight: bold; color: #dc3545;">%s</div>
                <p>Este código expirará en <strong>30 segundos</strong>.</p>
            </div>
        """.formatted(token);

        // Construye el mensaje de correo electrónico
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(content, true);

            // Envía el correo electrónico
            mailSender.send(message);
        } catch (MessagingException e) {
            // Maneja la excepción de envío de correo
            throw new RuntimeException("No se pudo enviar el correo: " + e.getMessage());
        }
    }
}
