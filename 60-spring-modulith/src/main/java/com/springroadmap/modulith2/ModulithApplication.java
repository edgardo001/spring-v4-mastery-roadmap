package com.springroadmap.modulith2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Modulo 60 - Spring Modulith avanzado (SIMULADO).
 *
 * LIMITACION IMPORTANTE:
 * Spring Modulith 1.x depende de Spring Boot 3.x. NO existe (aun) una release
 * compatible con Boot 4.1.0. Por eso este modulo IMPLEMENTA MANUALMENTE
 * los patrones cruciales de Modulith:
 *
 *   1) Event Publication Registry: tabla event_publication que persiste cada
 *      evento publicado y su timestamp de completado. Permite reprocesar
 *      eventos huerfanos tras un crash.
 *   2) Listener asincrono transaccional: @Async + @TransactionalEventListener
 *      (AFTER_COMMIT) que marca la publicacion como completada.
 *   3) Estructura por modulos (orders / notifications) con API publica y
 *      paquetes 'internal', igual que en el modulo 39.
 *
 * Cuando salga Spring Modulith 2.x (compat Boot 4), migrar es trivial: ver
 * README seccion "Patron de migracion".
 */
@SpringBootApplication
@EnableAsync
public class ModulithApplication {

    public static void main(String[] args) {
        SpringApplication.run(ModulithApplication.class, args);
    }
}
