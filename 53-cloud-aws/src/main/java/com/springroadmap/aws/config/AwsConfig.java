package com.springroadmap.aws.config;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.sqs.SqsClient;

/**
 * Configuración de los clientes AWS (S3, SQS, Secrets Manager).
 *
 * <p><b>Analogía del mundo real:</b> es como configurar tres "controles remotos" —
 * uno para el garaje (S3), uno para el correo (SQS), y uno para la caja fuerte (Secrets Manager).
 * Los tres se enchufan a la misma antena (endpoint) y usan las mismas pilas (credenciales).
 *
 * <p><b>Producción vs Desarrollo:</b>
 * <ul>
 *   <li><b>Desarrollo (este módulo)</b>: apunta a LocalStack en <code>http://localhost:4566</code>
 *       con credenciales dummy <code>test/test</code>.</li>
 *   <li><b>Producción</b>: NO se hardcodea nada. Se elimina <code>.endpointOverride(...)</code>
 *       y <code>.credentialsProvider(...)</code>, y se deja que el SDK use
 *       <code>DefaultCredentialsProvider</code> (busca en este orden: variables de entorno
 *       <code>AWS_ACCESS_KEY_ID/AWS_SECRET_ACCESS_KEY</code>, perfil <code>~/.aws/credentials</code>,
 *       IAM role del EC2/ECS/EKS).</li>
 * </ul>
 *
 * <p><b>ANTES (AWS SDK v1) vs AHORA (AWS SDK v2):</b>
 * <pre>
 *   // ANTES (SDK v1 — 2015):
 *   BasicAWSCredentials creds = new BasicAWSCredentials("test", "test");
 *   AmazonS3 s3 = AmazonS3ClientBuilder.standard()
 *       .withEndpointConfiguration(new EndpointConfiguration("http://localhost:4566", "us-east-1"))
 *       .withCredentials(new AWSStaticCredentialsProvider(creds))
 *       .build();
 *   s3.putObject("bucket", "key", "content");   // API con muchos overloads.
 *
 *   // AHORA (SDK v2 — 2018+):
 *   S3Client s3 = S3Client.builder()
 *       .endpointOverride(URI.create("http://localhost:4566"))
 *       .region(Region.US_EAST_1)
 *       .credentialsProvider(StaticCredentialsProvider.create(
 *               AwsBasicCredentials.create("test", "test")))
 *       .build();
 *   s3.putObject(PutObjectRequest.builder().bucket("b").key("k").build(),
 *                RequestBody.fromString("content"));   // API fluida + Request objects tipados.
 * </pre>
 */
// `@Configuration` = clase que declara @Bean. Spring la escanea al arrancar y registra
// cada método @Bean como un objeto singleton en el contenedor de dependencias.
@Configuration
public class AwsConfig {

    // `@Value("${...}")` = inyecta una propiedad del application.yml.
    // `final` = una vez asignado en el constructor, no cambia (inmutabilidad).
    private final String endpoint;
    private final String region;
    private final String accessKey;
    private final String secretKey;

    /**
     * Constructor injection (patrón recomendado por el equipo Spring desde 2018).
     * Ventajas vs @Autowired en el field:
     *  - Los campos pueden ser `final`.
     *  - En tests, se instancia con `new AwsConfig("http://...", "us-east-1", "test", "test")`.
     *  - Falla en tiempo de compilación si falta una dependencia.
     */
    public AwsConfig(
            @Value("${aws.endpoint}") String endpoint,
            @Value("${aws.region}") String region,
            @Value("${aws.access-key}") String accessKey,
            @Value("${aws.secret-key}") String secretKey) {
        this.endpoint = endpoint;
        this.region = region;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    /**
     * Construye el StaticCredentialsProvider a partir de las props inyectadas.
     * <p>PREGUNTA DE ALUMNO — "¿por qué un método privado y no un @Bean?"
     * Porque este objeto NO se comparte fuera de esta clase — solo lo usa los otros @Bean
     * de este mismo config. Si lo hiciéramos @Bean, aparecería en el contexto y confundiría.
     */
    private StaticCredentialsProvider credentials() {
        // `AwsBasicCredentials.create(...)` = método factory estático (equivalente moderno a `new AwsBasicCredentials(...)`).
        return StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey));
    }

    /**
     * Cliente S3 (Simple Storage Service).
     * <p><b>S3</b> es el servicio de almacenamiento de objetos de AWS: sube/baja archivos por clave.
     * Analogía: una bodega gigante donde cada caja tiene una etiqueta única (la "key").
     */
    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                // `URI.create(...)` = convierte String a URI. Solo se usa para LocalStack;
                // en producción se OMITE esta línea y el SDK usa el endpoint real (s3.us-east-1.amazonaws.com).
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region))
                .credentialsProvider(credentials())
                // `forcePathStyle(true)` = usa http://localhost:4566/bucket/key en vez de
                // http://bucket.localhost:4566/key (necesario para LocalStack).
                .forcePathStyle(true)
                .build();
    }

    /**
     * Cliente SQS (Simple Queue Service).
     * <p><b>SQS</b> es el servicio de colas de mensajes de AWS.
     * Analogía: un buzón donde un productor deja cartas y varios consumidores las retiran.
     */
    @Bean
    public SqsClient sqsClient() {
        return SqsClient.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region))
                .credentialsProvider(credentials())
                .build();
    }

    /**
     * Cliente Secrets Manager.
     * <p><b>Secrets Manager</b> guarda credenciales cifradas (DB passwords, API keys)
     * y las rota automáticamente.
     * Analogía: una caja fuerte con biométrico donde solo la aplicación autorizada (por su IAM role)
     * puede consultar el password de la base de datos.
     */
    @Bean
    public SecretsManagerClient secretsManagerClient() {
        return SecretsManagerClient.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region))
                .credentialsProvider(credentials())
                .build();
    }
}
