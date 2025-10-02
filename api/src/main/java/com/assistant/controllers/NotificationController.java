package com.assistant.controllers;

import com.assistant.entities.SlackNotificationPayload;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Component
public class NotificationController {

    private final WebClient webClient;

    private final Map<String, String> webhookUris = new HashMap<>();

    @Autowired
    public NotificationController(WebClient webClient) {
        this.webClient = webClient;
    }

    @PostConstruct
    public void init() {
        webhookUris.put("Frontend", "/T09JF0DCDNY/B09HW5W08UX/EmB4f1oX0CczZntj53v6k72V");
        webhookUris.put("Backend", "/T09JF0DCDNY/B09J57H66RH/ubG6Er0AN4sVfLgx5UlKYmN7");
        webhookUris.put("General", "/T09JF0DCDNY/B09J9GHGZ7G/VGxYInuo8DXzebfJywN7CGfY");
    }

    public void slackNotify(String type, String message) {
        String uri = webhookUris.get(type);

        if (uri == null) {
            System.err.println("Slack send error: Unknown notification type: " + type);
            return;
        }

        sendSlackMessage(uri, message, type);
    }

    private void sendSlackMessage(String uri, String message, String type) {
        SlackNotificationPayload payload = new SlackNotificationPayload(message);

        webClient.post()
                .uri(uri)
                .body(BodyInserters.fromValue(payload))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> Mono.error(new WebClientResponseException
                        (response.statusCode().value(), "Bad Request", null, null, null)))
                .onStatus(HttpStatusCode::is5xxServerError, response -> Mono.error(new WebClientResponseException
                        (response.statusCode().value(), "Server Error", null, null, null)))
                .bodyToMono(String.class)
                .subscribe(
                        response -> System.out.printf("Slack message sent to %s channel successfully: %s%n", type, response),
                        error -> System.err.printf("Slack send error to %s channel: %s%n", type, error.getMessage())
                );
    }
}
