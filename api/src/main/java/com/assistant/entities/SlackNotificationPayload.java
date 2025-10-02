package com.assistant.entities;

// SlackNotificationPayload.java
public class SlackNotificationPayload {
    private String text;

    public SlackNotificationPayload() {}

    public SlackNotificationPayload(String text) {
        this.text = text;
    }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}
