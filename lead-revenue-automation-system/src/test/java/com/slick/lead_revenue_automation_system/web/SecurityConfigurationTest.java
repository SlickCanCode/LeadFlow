package com.slick.lead_revenue_automation_system.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import com.slick.lead_revenue_automation_system.config.SecurityConfiguration;
import com.slick.lead_revenue_automation_system.services.AI.AiModelRouter.RoutedResponse;
import com.slick.lead_revenue_automation_system.services.MessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MessageController.class)
@AutoConfigureMockMvc
@Import(SecurityConfiguration.class)
class SecurityConfigurationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MessageService messageService;

    @Test
    void allowsWebChatFromConfiguredFrontendOrigin() throws Exception {
        when(messageService.handleWebChatMessage(any()))
                .thenReturn(new RoutedResponse("We can help.", "Gemini", "gemini-2.5-flash"));

        mockMvc.perform(post("/api/v1/message/web-chat/send")
                        .header("Origin", "http://localhost:3000")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"organizationId\":\"" + UUID.randomUUID()
                                + "\",\"externalId\":\"visitor-42\",\"message\":\"Can you help?\"}"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"));
    }

    @Test
    void allowsContactFormWithoutAuthentication() throws Exception {
        when(messageService.handleContactFormMessage(any()))
                .thenReturn(new RoutedResponse("Thanks for contacting us.", "Gemini", "gemini-2.5-flash"));

        mockMvc.perform(post("/api/v1/message/contact-form/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"organizationId\":\"" + UUID.randomUUID()
                                + "\",\"name\":\"Alex Rivera\",\"email\":\"alex@example.test\","
                                + "\"message\":\"I need a quote.\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void deniesOtherApiRoutesWithoutAuthentication() throws Exception {
        mockMvc.perform(post("/api/v1/message/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"organizationId\":\"" + UUID.randomUUID()
                                + "\",\"message\":\"Hello\"}"))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/v1/webhooks/whatsapp"))
                .andExpect(status().isForbidden());
    }

    @Test
    void rejectsCorsRequestsFromUnconfiguredOrigins() throws Exception {
        mockMvc.perform(options("/api/v1/message/web-chat/send")
                        .header("Origin", "https://unconfigured.example")
                        .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isForbidden());
    }
}
