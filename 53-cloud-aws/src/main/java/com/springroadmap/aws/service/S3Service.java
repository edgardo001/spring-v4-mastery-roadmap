package com.springroadmap.aws.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

/**
 * Servicio para interactuar con S3.
 *
 * <p><b>Analogía del mundo real:</b> es un empleado de bodega. Cuando le pasas una caja
 * (bytes) con una etiqueta (key), la guarda en el estante correcto (bucket). Cuando le
 * pides una caja por su etiqueta, va, la busca y te la trae.
 *
 * <p><b>ANTES (SDK v1) vs AHORA (SDK v2):</b>
 * <pre>
 *   // ANTES (v1):
 *   s3.putObject("bucket", "key", new ByteArrayInputStream(bytes), new ObjectMetadata());
 *   S3Object obj = s3.getObject("bucket", "key");
 *   byte[] data = IOUtils.toByteArray(obj.getObjectContent());
 *
 *   // AHORA (v2) — Request objects tipados + RequestBody/ResponseBytes:
 *   s3.putObject(PutObjectRequest.builder().bucket(b).key(k).build(),
 *                RequestBody.fromBytes(bytes));
 *   byte[] data = s3.getObjectAsBytes(GetObjectRequest.builder().bucket(b).key(k).build()).asByteArray();
 * </pre>
 */
// `@Service` = @Component especializado. Marca la clase como servicio de negocio.
// Spring la detecta en el component scan y la instancia como singleton.
@Service
public class S3Service {

    // Cliente S3 inyectado (construido en AwsConfig).
    private final S3Client s3Client;
    // Bucket por defecto (leído de application.yml).
    private final String bucket;

    // Constructor injection — patrón obligatorio del roadmap.
    public S3Service(S3Client s3Client, @Value("${aws.s3-bucket}") String bucket) {
        this.s3Client = s3Client;
        this.bucket = bucket;
    }

    /**
     * Sube un objeto a S3.
     *
     * @param key     nombre/clave del objeto (ej: "reports/2026-07-10.pdf").
     * @param content contenido en bytes.
     */
    public void upload(String key, byte[] content) {
        // `PutObjectRequest.builder()...build()` = patrón Builder (moderno en SDK v2).
        // Reemplaza al constructor con muchos parámetros del SDK v1.
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        // `RequestBody.fromBytes(...)` = envuelve el arreglo de bytes en el tipo esperado por el SDK.
        // También existen: fromString, fromFile, fromInputStream.
        s3Client.putObject(request, RequestBody.fromBytes(content));
    }

    /**
     * Descarga un objeto de S3 como arreglo de bytes.
     *
     * @param key clave del objeto a descargar.
     * @return contenido en bytes.
     */
    public byte[] download(String key) {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        // `getObjectAsBytes(...)` = conveniencia del SDK v2 que descarga TODO el objeto a memoria.
        // Para archivos grandes, usar `getObject(request, ResponseTransformer.toFile(path))`.
        // `ResponseBytes<GetObjectResponse>` = tipo genérico que envuelve los bytes + metadatos de respuesta.
        ResponseBytes<GetObjectResponse> response = s3Client.getObjectAsBytes(request);
        return response.asByteArray();
    }

    /** Getter del bucket configurado (útil para tests y logs). */
    public String getBucket() {
        return bucket;
    }
}
