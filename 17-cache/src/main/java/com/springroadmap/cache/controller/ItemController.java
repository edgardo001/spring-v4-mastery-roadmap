package com.springroadmap.cache.controller;

import com.springroadmap.cache.service.SlowService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints REST que demuestran el caché.
 *
 * IMPORTANTE: llamamos al service desde una clase distinta (controller -> service)
 * para que el proxy AOP de Spring pueda interceptar la llamada. Llamar al método
 * @Cacheable desde otro método de la misma clase evita el proxy y desactiva el caché.
 */
@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final SlowService service;

    public ItemController(SlowService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.getItem(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.invalidate(id);
        return ResponseEntity.noContent().build();
    }
}
