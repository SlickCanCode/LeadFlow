package com.slick.lead_revenue_automation_system.web;

import static org.mockito.ArgumentMatchers.any;
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

import com.slick.lead_revenue_automation_system.dto.CreateOrganizationdto;
import com.slick.lead_revenue_automation_system.dto.CreateOrganizationSettingsRequest;
import com.slick.lead_revenue_automation_system.dto.OrganizationResponse;
import com.slick.lead_revenue_automation_system.dto.OrganizationSettingsResponse;
import com.slick.lead_revenue_automation_system.entities.enums.AITone;
import com.slick.lead_revenue_automation_system.exception.OrganizationNotFoundException;
import com.slick.lead_revenue_automation_system.exception.OrganizationSettingsConflictException;
import com.slick.lead_revenue_automation_system.services.OrganizationService;

@WebMvcTest(OrganizationController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrganizationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrganizationService organizationService;

    @Test
    void createsOrganizationWithOnlyBusinessName() throws Exception {
        when(organizationService.createOrganization(any(CreateOrganizationdto.class)))
                .thenReturn(new OrganizationResponse(
                        UUID.fromString("53c13798-92d1-4f0a-9f9a-9a20f7153592"),
                        "Acme Services",
                        "acme-services",
                        null,
                        null,
                        null,
                        null,
                        "UTC",
                        "USD",
                        null,
                        null));

        mockMvc.perform(post("/api/v1/organization")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Acme Services\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Acme Services"))
                .andExpect(jsonPath("$.slug").value("acme-services"));
    }

    @Test
    void rejectsBlankBusinessNameUsingSharedErrorResponse() throws Exception {
        mockMvc.perform(post("/api/v1/organization")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"   \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").value("Organization name is required"));

        verify(organizationService, never()).createOrganization(any(CreateOrganizationdto.class));
    }

    @Test
    void createsOrganizationSettingsWithDefaults() throws Exception {
        UUID organizationId = UUID.fromString("53c13798-92d1-4f0a-9f9a-9a20f7153592");
        when(organizationService.createOrganizationSettings(
                org.mockito.ArgumentMatchers.eq(organizationId), any(CreateOrganizationSettingsRequest.class)))
                .thenReturn(new OrganizationSettingsResponse(
                        UUID.fromString("62d248a9-a3e2-40b1-8f0a-0b30f8264603"),
                        organizationId,
                        null,
                        null,
                        AITone.PROFESSIONAL,
                        false,
                        true,
                        java.util.Map.of(),
                        null,
                        null));

        mockMvc.perform(post("/api/v1/organization/{organizationId}/settings", organizationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.organizationId").value(organizationId.toString()))
                .andExpect(jsonPath("$.aiTone").value("PROFESSIONAL"))
                .andExpect(jsonPath("$.autoReplyEnabled").value(false))
                .andExpect(jsonPath("$.requireHumanApproval").value(true))
                .andExpect(jsonPath("$.settings").isEmpty());
    }

    @Test
    void rejectsInvalidSettingsToneAsBadRequest() throws Exception {
        UUID organizationId = UUID.fromString("53c13798-92d1-4f0a-9f9a-9a20f7153592");

        mockMvc.perform(post("/api/v1/organization/{organizationId}/settings", organizationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"aiTone\":\"LOUD\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").value("Request body contains invalid or malformed values."));
    }

    @Test
    void returnsNotFoundWhenOrganizationDoesNotExist() throws Exception {
        UUID organizationId = UUID.fromString("53c13798-92d1-4f0a-9f9a-9a20f7153592");
        when(organizationService.createOrganizationSettings(
                org.mockito.ArgumentMatchers.eq(organizationId), any(CreateOrganizationSettingsRequest.class)))
                .thenThrow(new OrganizationNotFoundException(organizationId));

        mockMvc.perform(post("/api/v1/organization/{organizationId}/settings", organizationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0]").value("Organization " + organizationId + " was not found."));
    }

    @Test
    void returnsConflictWhenSettingsAlreadyExist() throws Exception {
        UUID organizationId = UUID.fromString("53c13798-92d1-4f0a-9f9a-9a20f7153592");
        when(organizationService.createOrganizationSettings(
                org.mockito.ArgumentMatchers.eq(organizationId), any(CreateOrganizationSettingsRequest.class)))
                .thenThrow(new OrganizationSettingsConflictException(organizationId));

        mockMvc.perform(post("/api/v1/organization/{organizationId}/settings", organizationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errors[0]").value("Settings already exist for organization "
                        + organizationId + "."));
    }

        @Test
        void rejectsMalformedOrganizationIdAsBadRequest() throws Exception {
                mockMvc.perform(post("/api/v1/organization/not-a-uuid/settings")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content("{}"))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.errors[0]").value("A path parameter has an invalid format."));
        }
}