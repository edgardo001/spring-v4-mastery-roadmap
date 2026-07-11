package com.springroadmap.grpc.grpc;

import com.springroadmap.grpc.service.HelloService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Componente que representa el "servidor gRPC" logico del modulo.
 *
 * <p>ANALOGIA: piensa en este componente como la "boca del oficinista" que
 * atiende llamadas telefonicas. En el modulo REAL, esa boca es un
 * {@code io.grpc.Server} escuchando en el puerto 9090 y respondiendo con
 * mensajes binarios Protobuf. Aqui la simulamos con una llamada Java directa
 * al {@link HelloService}, para que el alumno vea el patron sin luchar con el
 * codegen de protobuf.
 *
 * <p>COMO SE CONECTARIA CON gRPC REAL:
 * <pre>
 * // 1. Definir el .proto (ver README) y generar stubs con protobuf-maven-plugin.
 * // 2. Extender la clase generada HelloServiceGrpc.HelloServiceImplBase:
 *
 * &#64;Override
 * public void sayHello(HelloRequest request, StreamObserver&lt;HelloResponse&gt; obs) {
 *     String msg = helloService.sayHello(request.getName());
 *     obs.onNext(HelloResponse.newBuilder().setMessage(msg).build());
 *     obs.onCompleted();
 * }
 *
 * // 3. Levantar el servidor Netty en &#64;PostConstruct:
 *
 * Server server = ServerBuilder.forPort(grpcPort)
 *         .addService(this)
 *         .build()
 *         .start();
 * </pre>
 *
 * <p>ANTES vs AHORA:
 * <pre>
 * // ANTES (Java 8) - inyeccion por campo con &#64;Autowired.
 * &#64;Autowired private HelloService helloService;
 *
 * // AHORA (Java 21) - inyeccion por constructor con 'final' + argumento tipado.
 * // Ventajas: inmutabilidad, testabilidad, deteccion en compilacion.
 * </pre>
 */
@Component
public class HelloGrpcServer {

    private static final Logger log = LoggerFactory.getLogger(HelloGrpcServer.class);

    private final HelloService helloService;
    private final int grpcPort;

    /**
     * Constructor injection - Spring inyecta las dependencias al crear el bean.
     *
     * @param helloService servicio de negocio inyectado
     * @param grpcPort     puerto configurado en application.yml (grpc.server.port)
     */
    public HelloGrpcServer(final HelloService helloService,
                           @Value("${grpc.server.port:9090}") final int grpcPort) {
        this.helloService = helloService;
        this.grpcPort = grpcPort;
    }

    /**
     * Metodo invocado por Spring cuando el bean queda listo.
     * En el modulo REAL, aqui haria {@code ServerBuilder.forPort(grpcPort).addService(this).build().start();}
     */
    @PostConstruct
    public void init() {
        log.info("[gRPC-demo] Servidor gRPC logico registrado en puerto {} (simulado).", grpcPort);
        log.info("[gRPC-demo] Para levantar un servidor gRPC REAL, ver README seccion 'Como escalar a gRPC vanilla'.");
    }

    /**
     * Simula el RPC {@code SayHello (HelloRequest) returns (HelloResponse)}.
     *
     * <p>En el gRPC REAL, este metodo recibiria un objeto HelloRequest generado por
     * protoc y responderia via {@code StreamObserver<HelloResponse>}. Aqui usamos
     * String plano por simplicidad del demo.
     *
     * @param name nombre a saludar (equivalente a HelloRequest.name)
     * @return saludo (equivalente a HelloResponse.message)
     */
    public String sayHello(final String name) {
        log.info("[gRPC-demo] RPC SayHello invocado con name={}", name);
        return helloService.sayHello(name);
    }
}
