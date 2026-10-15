package com.slick.lead_revenue_automation_system.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.slick.lead_revenue_automation_system.services.MessageService;

import lombok.RequiredArgsConstructor;
import tools.jackson.databind.JsonNode;

@RestController
@RequestMapping("api/v1/webhooks")
public class WebhooksController {

    @Autowired 
    private MessageService messageService;
    public WebhooksController(MessageService messageService) {
        this.messageService = messageService;
    }

    @Value("${whatsapp.webhook.verify-token}")
    private String verifyToken;

    @GetMapping("/whatsapp")
    public ResponseEntity<String> verifyWebhook(
            @RequestParam(name = "hub.mode", required = false) String mode,
            @RequestParam(name = "hub.verify_token", required = false) String token,
            @RequestParam(name = "hub.challenge", required = false) String challenge) {

        if ("subscribe".equals(mode)
                && verifyToken.equals(token)
                && challenge != null) {
            return ResponseEntity.ok(challenge);
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("");
    }

    @PostMapping("/whatsapp")
    public ResponseEntity<Void> receiveWebhook(
            @RequestBody JsonNode payload) {

        JsonNode entries = payload.path("entry");

        for (JsonNode entry : entries) {
            for (JsonNode change : entry.path("changes")) {

                JsonNode messages = change.path("value").path("messages");

                for (JsonNode message : messages) {
                    // This first version handles text messages only.
                    if (!"text".equals(message.path("type").asText())) {
                        continue;
                    }

                    String sender = message.path("from").asText();
                    String messageId = message.path("id").asText();
                    String text = message.path("text").path("body").asText();

                    if (sender.isBlank() || messageId.isBlank() || text.isBlank()) {
                        continue;
                    }

                    System.out.println("Received message from " + sender + ": " + text);
                    System.out.println("Response from AI model...: " + messageService.sendMessage(text).response());
                }
            }
        }

        return ResponseEntity.ok().build();
    }



}


