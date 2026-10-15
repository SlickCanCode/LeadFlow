package com.slick.lead_revenue_automation_system.web;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.slick.lead_revenue_automation_system.services.MessageService;
import com.slick.lead_revenue_automation_system.services.AI.AiModelRouter.RoutedResponse;

@WebMvcTest(MessageController.class)
@AutoConfigureMockMvc(addFilters = false)
class MessageControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MessageService messageService;

    @Test
    void sendsOrganizationIdAndCustomerMessageToService() throws Exception {
        UUID organizationId = UUID.fromString("53c13798-92d1-4f0a-9f9a-9a20f7153592");
        when(messageService.sendMessage(organizationId, "Do you offer repairs?"))
                .thenReturn(new RoutedResponse("Yes, we do.", "Gemini", "gemini-2.5-flash"));

        mockMvc.perform(post("/api/v1/message/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"organizationId\":\"" + organizationId
                                + "\",\"message\":\"Do you offer repairs?\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("Yes, we do."));

        verify(messageService).sendMessage(organizationId, "Do you offer repairs?");
    }

    @Test
    void rejectsMissingOrganizationIdAndBlankMessage() throws Exception {
        mockMvc.perform(post("/api/v1/message/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"message\":\"   \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.length()").value(2));

        verify(messageService, never()).sendMessage(org.mockito.ArgumentMatchers.any(UUID.class),
                org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void sendsWebChatRequestToMessageService() throws Exception {
        UUID organizationId = UUID.fromString("53c13798-92d1-4f0a-9f9a-9a20f7153592");
        when(messageService.handleWebChatMessage(org.mockito.ArgumentMatchers.any()))
                .thenReturn(new RoutedResponse("We can help.", "Gemini", "gemini-2.5-flash"));

        mockMvc.perform(post("/api/v1/message/web-chat/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"organizationId\":\"" + organizationId
                                + "\",\"externalId\":\"visitor-42\",\"message\":\"Can you help?\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("We can help."));
    }

    @Test
    void sendsContactFormRequestToMessageService() throws Exception {
        UUID organizationId = UUID.fromString("53c13798-92d1-4f0a-9f9a-9a20f7153592");
        when(messageService.handleContactFormMessage(org.mockito.ArgumentMatchers.any()))
                .thenReturn(new RoutedResponse("Thanks for contacting us.", "Gemini", "gemini-2.5-flash"));

        mockMvc.perform(post("/api/v1/message/contact-form/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"organizationId\":\"" + organizationId
                                + "\",\"name\":\"Alex Rivera\",\"email\":\"alex@example.test\","
                                + "\"message\":\"I need a quote.\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("Thanks for contacting us."));
    }
}