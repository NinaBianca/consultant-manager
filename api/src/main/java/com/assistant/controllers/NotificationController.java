package com.assistant.controllers;

import com.assistant.entities.SlackNotificationPayload;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Autowired
    private final WebClient webClient;

    // Map the constants used by the Camel router to the injected URIs
    private final Map<String, String> webhookUris;

    @Autowired
    public NotificationController(WebClient webClient,
                                  @Value("${slack.uri.frontend}") String frontendUri,
                                  @Value("${slack.uri.backend}") String backendUri,
                                  @Value("${slack.uri.general}") String generalUri) {
        this.webClient = webClient;

        // Initialize the map here using the injected values
        this.webhookUris = Map.of(
                "Frontend", frontendUri,
                "Backend", backendUri,
                "General", generalUri
        );
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
