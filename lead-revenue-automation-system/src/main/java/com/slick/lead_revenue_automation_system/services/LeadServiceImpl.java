package com.slick.lead_revenue_automation_system.services;

import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.slick.lead_revenue_automation_system.dto.CreateLeadRequest;
import com.slick.lead_revenue_automation_system.dto.LeadResponse;
import com.slick.lead_revenue_automation_system.entities.Lead;
import com.slick.lead_revenue_automation_system.entities.Organization;
import com.slick.lead_revenue_automation_system.entities.enums.LeadSource;
import com.slick.lead_revenue_automation_system.exception.LeadConflictException;
import com.slick.lead_revenue_automation_system.exception.OrganizationNotFoundException;
import com.slick.lead_revenue_automation_system.repositories.LeadRepository;
import com.slick.lead_revenue_automation_system.repositories.OrganizationRepository;
import com.slick.lead_revenue_automation_system.services.context.LeadConversationInfo;

@Service
public class LeadServiceImpl implements LeadService {
    private final LeadRepository leadRepository;
    private final OrganizationRepository organizationRepository;

    public LeadServiceImpl(LeadRepository leadRepository, OrganizationRepository organizationRepository) {
        this.leadRepository = leadRepository;
        this.organizationRepository = organizationRepository;
    }

    @Override
    @Transactional
    public LeadResponse createManualLead(CreateLeadRequest request) {
        Organization organization = getOrganization(request.organizationId());
        String[] nameParts = splitName(request.name());
        Lead lead = new Lead(organization, null, nameParts[0], nameParts[1],
                request.email(), request.phone(), request.source());
        return toResponse(leadRepository.saveAndFlush(lead));
    }

    @Override
    @Transactional
    public LeadConversationInfo findOrCreateWebChatLead(UUID organizationId, String externalId) {
        return leadRepository.findByOrganization_IdAndExternalId(organizationId, externalId)
                .map(LeadServiceImpl::toConversationInfo)
                .orElseGet(() -> {
                    Organization organization = getOrganization(organizationId);
                    Lead lead = new Lead(organization, externalId, "Web Chat Visitor", null,
                            null, null, LeadSource.WEBSITE_CHATBOX);
                    try {
                        return toConversationInfo(leadRepository.saveAndFlush(lead));
                    } catch (DataIntegrityViolationException exception) {
                        throw new LeadConflictException(
                                "A lead with this external ID already exists for the organization.", exception);
                    }
                });
    }

    @Override
    @Transactional
    public LeadConversationInfo createContactFormLead(UUID organizationId, String name, String email) {
        Organization organization = getOrganization(organizationId);
        String[] nameParts = splitName(name);
        Lead lead = new Lead(organization, null, nameParts[0], nameParts[1], email,
                null, LeadSource.WEBSITE_CONTACTFORM);
        return toConversationInfo(leadRepository.saveAndFlush(lead));
    }

    private Organization getOrganization(UUID organizationId) {
        return organizationRepository.findById(organizationId)
                .orElseThrow(() -> new OrganizationNotFoundException(organizationId));
    }

    private String[] splitName(String name) {
        String[] parts = name.trim().split("\\s+", 2);
        return new String[] { parts[0], parts.length == 1 ? null : parts[1] };
    }

    private LeadResponse toResponse(Lead lead) {
        return new LeadResponse(lead.getId(), lead.getOrganization().getId(), lead.getExternalId(),
                lead.getFirstName(), lead.getLastName(), lead.getEmail(), lead.getPhone(),
                lead.getSource(), lead.getStatus(), lead.getCreatedAt());
    }

    private static LeadConversationInfo toConversationInfo(Lead lead) {
        return new LeadConversationInfo(lead.getId(), lead.getExternalId(), lead.getFirstName(),
                lead.getLastName(), lead.getEmail(), lead.getPhone(), lead.getSource(), lead.getStatus());
    }
}