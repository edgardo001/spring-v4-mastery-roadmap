package com.springroadmap.k8s;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Modulo 48 - Punto de entrada de la aplicacion Spring Boot.
 *
 * No hay nada "especial de Kubernetes" en este codigo Java: la app se
 * comporta EXACTAMENTE igual corriendo con "java -jar" en tu portatil
 * que dentro de un Pod. La unica diferencia es que en Kubernetes, el
 * orquestador consulta periodicamente /actuator/health/liveness y
 * /actuator/health/readiness para decidir si reiniciar el pod o si
 * enrutarle trafico.
 */
@SpringBootApplication
public class KubernetesApplication {

    public static void main(String[] args) {
        SpringApplication.run(KubernetesApplication.class, args);
    }
}
