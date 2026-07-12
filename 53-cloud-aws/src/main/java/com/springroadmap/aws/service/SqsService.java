package com.springroadmap.aws.service;

import java.util.List;

import org.springframework.stereotype.Service;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

/**
 * Servicio para interactuar con SQS (Simple Queue Service).
 *
 * <p><b>Analogía del mundo real:</b> un tablero de recados. El productor pega una nota
 * (sendMessage) y los consumidores la leen y descuelgan (receiveMessages + deleteMessage).
 *
 * <p><b>ANTES (SDK v1) vs AHORA (SDK v2):</b>
 * <pre>
 *   // ANTES (v1):
 *   sqs.sendMessage(new SendMessageRequest(queueUrl, body));
 *   List&lt;Message&gt; msgs = sqs.receiveMessage(new ReceiveMessageRequest(queueUrl)
 *       .withMaxNumberOfMessages(10)).getMessages();
 *
 *   // AHORA (v2) — Builder pattern + inmutabilidad:
 *   sqs.sendMessage(SendMessageRequest.builder().queueUrl(url).messageBody(body).build());
 *   List&lt;Message&gt; msgs = sqs.receiveMessage(ReceiveMessageRequest.builder()
 *       .queueUrl(url).maxNumberOfMessages(10).build()).messages();
 * </pre>
 */
@Service
public class SqsService {

    private final SqsClient sqsClient;

    public SqsService(SqsClient sqsClient) {
        this.sqsClient = sqsClient;
    }

    /**
     * Envía un mensaje a la cola indicada.
     *
     * @param queueUrl URL completa de la cola (ej: "http://localhost:4566/000000000000/mi-cola").
     * @param body     texto del mensaje (típicamente JSON).
     */
    public void sendMessage(String queueUrl, String body) {
        SendMessageRequest request = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(body)
                .build();
        sqsClient.sendMessage(request);
    }

    /**
     * Recibe hasta 10 mensajes de la cola (long-polling deshabilitado por simplicidad).
     *
     * @param queueUrl URL completa de la cola.
     * @return lista de mensajes recibidos. Vacía si no hay nada.
     */
    public List<Message> receiveMessages(String queueUrl) {
        ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                // `maxNumberOfMessages(10)` = tope duro de SQS. No admite más de 10 por llamada.
                .maxNumberOfMessages(10)
                // `waitTimeSeconds(0)` = short-polling. En producción, usar 20 para long-polling
                // (menos llamadas API = menos costo).
                .waitTimeSeconds(0)
                .build();

        // `.messages()` en SDK v2 (getMessages() era v1). Estilo "sin prefijo get" = record-like.
        return sqsClient.receiveMessage(request).messages();
    }
}
