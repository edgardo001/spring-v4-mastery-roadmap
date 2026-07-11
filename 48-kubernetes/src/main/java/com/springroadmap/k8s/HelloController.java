package com.springroadmap.k8s;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Modulo 48 - Controlador de humo.
 *
 * Un unico endpoint que responde texto plano. Sirve para verificar que
 * el Pod esta REALMENTE aceptando trafico (mas alla del readinessProbe):
 * si haces "kubectl port-forward svc/kubernetes-service 8080:80" y
 * curl http://localhost:8080/api/hello obtiene "Hello from K8s pod",
 * entonces Service -> Pod -> Spring MVC funciona end-to-end.
 */
@RestController
@RequestMapping("/api")
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello from K8s pod";
    }
}
