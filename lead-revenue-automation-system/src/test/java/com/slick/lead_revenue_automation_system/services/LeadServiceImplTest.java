package com.slick.lead_revenue_automation_system.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.slick.lead_revenue_automation_system.dto.CreateLeadRequest;
import com.slick.lead_revenue_automation_system.entities.Lead;
import com.slick.lead_revenue_automation_system.entities.Organization;
import com.slick.lead_revenue_automation_system.entities.enums.LeadSource;
import com.slick.lead_revenue_automation_system.repositories.LeadRepository;
import com.slick.lead_revenue_automation_system.repositories.OrganizationRepository;

@ExtendWith(MockitoExtension.class)
class LeadServiceImplTest {
    @Mock
    private LeadRepository leadRepository;

    @Mock
    private OrganizationRepository organizationRepository;

    @InjectMocks
    private LeadServiceImpl leadService;

    @Test
    void createsManualLeadWithOptionalPhoneOrEmailAndSplitsName() {
        UUID organizationId = UUID.fromString("53c13798-92d1-4f0a-9f9a-9a20f7153592");
        Organization organization = new Organization("Acme", "acme", null, null, null, null, null, null);
        when(organizationRepository.findById(organizationId)).thenReturn(Optional.of(organization));
        when(leadRepository.saveAndFlush(any(Lead.class))).thenAnswer(invocation -> invocation.getArgument(0));

        leadService.createManualLead(new CreateLeadRequest(
                organizationId, "Alex Rivera", "alex@example.test", null, LeadSource.MANUAL));

        ArgumentCaptor<Lead> leadCaptor = ArgumentCaptor.forClass(Lead.class);
        verify(leadRepository).saveAndFlush(leadCaptor.capture());
        Lead savedLead = leadCaptor.getValue();
        assertEquals(organization, savedLead.getOrganization());
        assertEquals("Alex", savedLead.getFirstName());
        assertEquals("Rivera", savedLead.getLastName());
        assertEquals("alex@example.test", savedLead.getEmail());
        assertNull(savedLead.getPhone());
        assertEquals(LeadSource.MANUAL, savedLead.getSource());
    }

    @Test
    void reusesWebChatLeadScopedToOrganization() {
        UUID organizationId = UUID.fromString("53c13798-92d1-4f0a-9f9a-9a20f7153592");
        Lead existingLead = new Lead(null, "visitor-42", "Alex", null, null, null, LeadSource.WEBSITE_CHATBOX);
        when(leadRepository.findByOrganization_IdAndExternalId(organizationId, "visitor-42"))
                .thenReturn(Optional.of(existingLead));

        var result = leadService.findOrCreateWebChatLead(organizationId, "visitor-42");

        assertEquals("visitor-42", result.externalId());
        assertEquals("Alex", result.firstName());
        verify(organizationRepository, never()).findById(organizationId);
        verify(leadRepository, never()).saveAndFlush(any(Lead.class));
    }

    @Test
    void createsWebChatLeadWhenExternalIdIsNewForOrganization() {
        UUID organizationId = UUID.fromString("53c13798-92d1-4f0a-9f9a-9a20f7153592");
        Organization organization = new Organization("Acme", "acme", null, null, null, null, null, null);
        when(leadRepository.findByOrganization_IdAndExternalId(organizationId, "visitor-42"))
                .thenReturn(Optional.empty());
        when(organizationRepository.findById(organizationId)).thenReturn(Optional.of(organization));
        when(leadRepository.saveAndFlush(any(Lead.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = leadService.findOrCreateWebChatLead(organizationId, "visitor-42");

        assertEquals("visitor-42", result.externalId());
        assertEquals("Web Chat Visitor", result.firstName());
        assertEquals(LeadSource.WEBSITE_CHATBOX, result.source());
    }

    @Test
    void createsContactFormLeadWithEmailAndContactFormSource() {
        UUID organizationId = UUID.fromString("53c13798-92d1-4f0a-9f9a-9a20f7153592");
        Organization organization = new Organization("Acme", "acme", null, null, null, null, null, null);
        when(organizationRepository.findById(organizationId)).thenReturn(Optional.of(organization));
        when(leadRepository.saveAndFlush(any(Lead.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = leadService.createContactFormLead(organizationId, "Alex Rivera", "alex@example.test");

        assertEquals("Alex", result.firstName());
        assertEquals("Rivera", result.lastName());
        assertEquals("alex@example.test", result.email());
        assertEquals(LeadSource.WEBSITE_CONTACTFORM, result.source());
    }
}