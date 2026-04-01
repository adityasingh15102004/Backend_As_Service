package com.jobhunt.saas.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class EmailService {

    @Value("${resend.api.key}")
    private String resendApiKey;

    @Value("${spring.mail.from:onboarding@resend.dev}")
    private String fromEmail;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(java.time.Duration.ofSeconds(10))
            .build();

    public void sendEmail(String to, String subject, String text) {
        try {
            String jsonPayload = String.format(
                "{\"from\": \"%s\", \"to\": [\"%s\"], \"subject\": \"%s\", \"text\": \"%s\"}",
                fromEmail, to, subject, text
            );

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.resend.com/emails"))
                .header("Authorization", "Bearer " + resendApiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

            log.info("Sending email via Resend API to: {}", to);
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                log.info("Email sent successfully via Resend API. Response: {}", response.body());
            } else {
                log.error("Failed to send email via Resend API. Status: {}, Body: {}", response.statusCode(), response.body());
                throw new RuntimeException("Email service error: " + response.body());
            }
        } catch (Exception e) {
            log.error("Error occurred while sending email via Resend API", e);
            throw new RuntimeException("Could not send email", e);
        }
    }
}
