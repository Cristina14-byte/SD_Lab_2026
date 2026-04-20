package com.chris.sd_assignment1.model.services;

import com.chris.sd_assignment1.model.events.EventListener;
import com.chris.sd_assignment1.model.events.ItemEvent;

public class NotificationService implements EventListener {

    private final EmailService emailService;

    public NotificationService() {
        this.emailService = new EmailService();
    }

    @Override
    public void onEvent(ItemEvent event) {
        String action = event.getType().toString().toLowerCase();
        String itemName = event.getItem() != null ? event.getItem().getName() : "Unknown Item";
        String userEmail = event.getUser() != null ? event.getUser().getEmail() : "test@example.com";

        String subject = "TCG Shop Alert - Item " + action;
        String body = "Hello " + event.getUser().getUsername() + ",\n\n" +
                "The item '" + itemName + "' was successfully " + action + " in the system.\n\n" +
                "Thank you,\nTCG Shop Admin";

        emailService.sendEmailWithAttachment(userEmail, subject, body, null);
    }
}