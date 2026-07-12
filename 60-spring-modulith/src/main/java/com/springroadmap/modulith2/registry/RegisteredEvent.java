package com.springroadmap.modulith2.registry;

/**
 * Wrapper de evento que lleva el id de la publicacion registrada.
 *
 * Sirve para que el listener async pueda cerrar (markCompleted) el registro
 * cuando termine su trabajo, imitando el comportamiento de
 * @ApplicationModuleListener de Spring Modulith.
 */
public record RegisteredEvent(Long publicationId, Object payload) {
}
