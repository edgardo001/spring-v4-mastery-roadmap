package com.springroadmap.restclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la aplicacion.
 *
 * Antes vs Ahora:
 *  - Antes: se instanciaba `new RestTemplate()` (deprecated desde Spring 6.1) o se
 *    configuraba un bean RestTemplate con RestTemplateBuilder.
 *  - Ahora: RestClient reemplaza a RestTemplate con una API fluida y una version
 *    "interface-based" (@HttpExchange + HttpServiceProxyFactory) al estilo Feign.
 */
@SpringBootApplication
public class RestClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestClientApplication.class, args);
    }
}
