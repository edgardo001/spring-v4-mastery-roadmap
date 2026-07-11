package com.springroadmap.mail.service;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import jakarta.mail.Session;
import java.util.Properties;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EmailServiceTest {

    private JavaMailSender sender;
    private EmailService service;

    @BeforeEach
    void setUp() {
        sender = mock(JavaMailSender.class);
        service = new EmailService(sender);
    }

    @Test
    void sendSimple_debeInvocarSendConSimpleMailMessage() {
        service.sendSimple("a@b.cl", "hi", "hello");

        verify(sender).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendHtml_debeInvocarSendConMimeMessage() {
        MimeMessage mime = new MimeMessage(Session.getInstance(new Properties()));
        when(sender.createMimeMessage()).thenReturn(mime);

        service.sendHtml("a@b.cl", "hi", "<h1>hello</h1>");

        verify(sender).send(any(MimeMessage.class));
    }
}
