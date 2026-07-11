package com.springroadmap.resilience.controller;

import com.springroadmap.resilience.service.ResilientClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller HTTP que expone la operación protegida.
 *
 * <p><b>Analogía:</b> la "campanilla del mostrador" — un cliente la toca
 * (GET /api/resilience/call), y en el fondo del local el guardia + ayudante
 * (ResilientClient) se encargan de que la respuesta salga aunque el WiFi
 * del café esté con hipo.</p>
 */
@RestController  // @RestController = @Controller + @ResponseBody: retorna JSON/texto, no vistas.
@RequestMapping("/api/resilience")  // Prefijo común para todos los endpoints de esta clase.
public class ResilienceController {

    private final ResilientClient resilientClient;

    // Constructor injection — Spring 4+ ya no exige @Autowired si hay un único constructor.
    public ResilienceController(ResilientClient resilientClient) {
        this.resilientClient = resilientClient;
    }

    /**
     * GET /api/resilience/call — invoca la llamada protegida.
     * @return texto plano con la respuesta del FlakyService o propaga excepción
     *         si Retry se rindió y el CB no logró recuperarse.
     */
    @GetMapping("/call")
    public String call() {
        return resilientClient.callWithProtection();
    }
}
