package com.springroadmap.featureflags.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.springroadmap.featureflags.service.FeatureFlagsService;

/**
 * CheckoutController — demuestra el uso de Feature Flags en un endpoint web.
 *
 * ANALOGÍA:
 * Un restaurante con dos cocinas: la clásica y la experimental. El mesero (el
 * controller) mira el "flag del día" y sirve el plato correspondiente. Cambiar
 * de cocina no requiere remodelar el restaurante, solo mover una palanca.
 *
 * ENDPOINTS:
 *   GET  /api/checkout                       -> "legacy checkout" o "beta checkout"
 *   POST /admin/flags/{flag}?enabled=true    -> cambia un flag en runtime
 *
 * ANTES (Java 8) vs AHORA (Java 21):
 *   // Antes: @RequestMapping(value="/api/checkout", method=RequestMethod.GET)
 *   // Ahora: @GetMapping("/api/checkout") — atajo introducido en Spring 4.3.
 */
@RestController
public class CheckoutController {

    private final FeatureFlagsService flags;

    public CheckoutController(final FeatureFlagsService flags) {
        this.flags = flags;
    }

    /**
     * Endpoint principal de checkout.
     * Retorna un string plano (Spring lo serializa como text/plain o JSON string)
     * dependiendo del estado del flag "betaCheckout".
     */
    @GetMapping("/api/checkout")
    public String checkout() {
        // if clásico: sigue siendo la forma más clara y legible.
        if (flags.isEnabled("betaCheckout")) {
            return "beta checkout";
        }
        return "legacy checkout";
    }

    /**
     * Endpoint admin para cambiar un flag en caliente.
     *
     * IMPORTANTE (Seguridad):
     * En producción este endpoint debe protegerse con Spring Security + rol
     * ADMIN, y jamás quedar expuesto sin auth. Aquí lo dejamos abierto solo
     * con fines didácticos (demo del toggle en runtime).
     *
     * Ejemplo:
     *   curl -X POST "http://localhost:8080/admin/flags/betaCheckout?enabled=true"
     */
    @PostMapping("/admin/flags/{flag}")
    public String toggle(@PathVariable("flag") final String flag,
                         @RequestParam("enabled") final boolean enabled) {
        flags.setEnabled(flag, enabled);
        return "flag '" + flag + "' set to " + enabled;
    }
}
