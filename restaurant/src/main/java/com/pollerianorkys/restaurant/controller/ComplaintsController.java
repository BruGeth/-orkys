package com.pollerianorkys.restaurant.controller;

import com.pollerianorkys.restaurant.model.ReclamoQueja;
import com.pollerianorkys.restaurant.repository.ReclamoQuejaRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

/**
 * Controlador para manejar el libro de reclamaciones
 */
@Controller
@RequestMapping("/complaints")
@RequiredArgsConstructor
public class ComplaintsController {

    private final ReclamoQuejaRepository reclamorepositorio;
    /**
     * Muestra la página del libro de reclamaciones
     */
    @GetMapping("/book")
    public String showComplaintsBookPage(Model model) {
        // Aquí podrías agregar datos al modelo si es necesario
        return "complaintsBook";
    }

    /**
     * Procesa el envío de una reclamación
     */
  @PostMapping("/submit")
    public String processComplaint(@RequestParam Map<String, String> formData,
                                   RedirectAttributes redirectAttributes) {

        try {
            ReclamoQueja reclamo = new ReclamoQueja();
            reclamo.setRestaurante(formData.get("restaurante"));
            reclamo.setNombreCompleto(formData.get("nombreCompleto"));
            reclamo.setDepartamento(formData.get("departamento"));
            reclamo.setDireccion(formData.get("direccion"));
            reclamo.setDocumento(formData.get("documento"));
            reclamo.setTelefono(formData.get("telefono"));
            reclamo.setEmail(formData.get("email"));
            reclamo.setCelular(formData.get("celular"));
            reclamo.setTipo(formData.get("tipo"));
            reclamo.setNumeroPedido(formData.get("numeroPedido"));

            // Manejo de fecha del pedido
            if (formData.get("fechaPedido") != null && !formData.get("fechaPedido").isEmpty()) {
                reclamo.setFechaPedido(LocalDate.parse(formData.get("fechaPedido")));
            }

            reclamo.setCanalPedido(formData.get("canalPedido"));
            reclamo.setMontoReclamado(formData.get("montoReclamado"));
            reclamo.setTipoReclamo(formData.get("tipoReclamo"));
            reclamo.setDetalleReclamo(formData.get("detalleReclamo"));

            // Manejo de fecha de observación
            if (formData.get("fechaObservacion") != null && !formData.get("fechaObservacion").isEmpty()) {
                reclamo.setFechaObservacion(LocalDate.parse(formData.get("fechaObservacion")));
            }

            reclamo.setObservaciones(formData.get("observaciones"));
            reclamo.setConsentimiento(formData.containsKey("consentimiento"));

            reclamorepositorio.save(reclamo);
            redirectAttributes.addFlashAttribute("success", "¡Reclamo enviado exitosamente!");

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Hubo un error al enviar el reclamo.");
        }

        return "redirect:/complaints/book";
    }
}


