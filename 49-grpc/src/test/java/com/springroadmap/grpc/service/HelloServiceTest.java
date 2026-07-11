package com.springroadmap.grpc.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests unitarios PUROS del servicio de negocio.
 * No arrancan Spring, no usan Mockito: JUnit puro sobre la clase concreta.
 */
class HelloServiceTest {

    private final HelloService service = new HelloServiceImpl();

    @Test
    void sayHelloBuildsGreetingWithName() {
        final String result = service.sayHello("Juan");
        assertTrue(result.contains("Juan"), "El saludo debe contener el nombre");
        assertEquals("Hola, Juan! (via gRPC-demo)", result);
    }

    @Test
    void sayHelloRejectsNull() {
        assertThrows(IllegalArgumentException.class, () -> service.sayHello(null));
    }

    @Test
    void sayHelloRejectsBlank() {
        assertThrows(IllegalArgumentException.class, () -> service.sayHello("   "));
    }
}
