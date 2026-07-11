package com.springroadmap.grpc.service;

/**
 * Contrato del servicio Hello.
 *
 * <p>ANALOGIA: es la "carta de un restaurante". Define QUE se puede pedir
 * (metodos), pero no COMO se cocina (implementacion).
 *
 * <p>En un modulo gRPC REAL, este contrato viviria en el archivo
 * {@code src/main/proto/hello.proto} y {@code protoc} generaria automaticamente
 * la clase base {@code HelloServiceGrpc.HelloServiceImplBase} que la implementacion
 * heredaria. Aqui lo dejamos como {@code interface} Java para no depender del
 * codegen de protobuf.
 *
 * <p>ANTES vs AHORA:
 * <pre>
 * // ANTES (Java 8) - interfaces sin ningun feature moderno, iguales a hoy.
 * public interface HelloService { String sayHello(String name); }
 *
 * // AHORA (Java 21) - podriamos usar 'sealed interface' para restringir quien
 * // puede implementarla, pero para un servicio publico normal no aporta.
 * </pre>
 */
public interface HelloService {

    /**
     * Devuelve un saludo personalizado.
     *
     * <p>Este metodo es el equivalente Java del RPC:
     * <pre>
     *   rpc SayHello (HelloRequest) returns (HelloResponse);
     * </pre>
     *
     * @param name nombre de la persona a saludar (no debe ser null ni vacio).
     * @return mensaje de saludo con el nombre incrustado.
     */
    String sayHello(String name);
}
