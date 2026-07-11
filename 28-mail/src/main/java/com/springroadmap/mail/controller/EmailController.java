package com.springroadmap.mail.controller;

import com.springroadmap.mail.dto.EmailRequest;
import com.springroadmap.mail.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/emails")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/simple")
    public ResponseEntity<Map<String, String>> sendSimple(@RequestBody EmailRequest request) {
        emailService.sendSimple(request.getTo(), request.getSubject(), request.getBody());
        return ResponseEntity.ok(Map.of("status", "sent", "type", "simple", "to", request.getTo()));
    }

    @PostMapping("/html")
    public ResponseEntity<Map<String, String>> sendHtml(@RequestBody EmailRequest request) {
        emailService.sendHtml(request.getTo(), request.getSubject(), request.getBody());
        return ResponseEntity.ok(Map.of("status", "sent", "type", "html", "to", request.getTo()));
    }
}
