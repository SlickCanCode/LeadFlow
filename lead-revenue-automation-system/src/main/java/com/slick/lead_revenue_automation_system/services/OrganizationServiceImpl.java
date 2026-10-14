package com.slick.lead_revenue_automation_system.services;

import java.text.Normalizer;
import java.util.Locale;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.slick.lead_revenue_automation_system.dto.CreateOrganizationdto;
import com.slick.lead_revenue_automation_system.dto.CreateOrganizationSettingsRequest;
import com.slick.lead_revenue_automation_system.dto.OrganizationBusinessInfo;
import com.slick.lead_revenue_automation_system.dto.OrganizationResponse;
import com.slick.lead_revenue_automation_system.dto.OrganizationSettingsResponse;
import com.slick.lead_revenue_automation_system.entities.Organization;
import com.slick.lead_revenue_automation_system.entities.OrganizationSettings;
import com.slick.lead_revenue_automation_system.exception.OrganizationConflictException;
import com.slick.lead_revenue_automation_system.exception.OrganizationBusinessSettingsRequiredException;
import com.slick.lead_revenue_automation_system.exception.OrganizationNotFoundException;
import com.slick.lead_revenue_automation_system.exception.OrganizationSettingsConflictException;
import com.slick.lead_revenue_automation_system.repositories.OrganizationRepository;
import com.slick.lead_revenue_automation_system.repositories.OrganizationSettingsRepository;
import com.slick.lead_revenue_automation_system.entities.enums.AITone;

@Service
public class OrganizationServiceImpl implements OrganizationService {
    private static final int MAX_SLUG_LENGTH = 150;

    private final OrganizationRepository organizationRepository;
    private final OrganizationSettingsRepository organizationSettingsRepository;

    public OrganizationServiceImpl(OrganizationRepository organizationRepository,
            OrganizationSettingsRepository organizationSettingsRepository) {
        this.organizationRepository = organizationRepository;
        this.organizationSettingsRepository = organizationSettingsRepository;
    }

    @Override
    @Transactional
    public OrganizationResponse createOrganization(CreateOrganizationdto request) {
        String slug = createUniqueSlug(request.name());
        Organization organization = new Organization(
                request.name(),
                slug,
                request.email(),
                request.phone(),
                request.address(),
                request.logoUrl(),
                request.timezone(),
                request.currency());

        Organization savedOrganization;
        try {
            savedOrganization = organizationRepository.saveAndFlush(organization);
        } catch (DataIntegrityViolationException exception) {
            throw new OrganizationConflictException(
                    "The organization could not be created because its URL conflicts with another organization.",
                    exception);
        }

        return new OrganizationResponse(
                savedOrganization.getId(),
                savedOrganization.getName(),
                savedOrganization.getSlug(),
                savedOrganization.getEmail(),
                savedOrganization.getPhone(),
                savedOrganization.getAddress(),
                savedOrganization.getLogoUrl(),
                savedOrganization.getTimezone(),
                savedOrganization.getCurrency(),
                savedOrganization.getCreatedAt(),
                savedOrganization.getUpdatedAt());
    }

    @Override
    @Transactional
    public OrganizationSettingsResponse createOrganizationSettings(
            UUID organizationId, CreateOrganizationSettingsRequest request) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new OrganizationNotFoundException(organizationId));

        if (organizationSettingsRepository.existsByOrganization_Id(organizationId)) {
            throw new OrganizationSettingsConflictException(organizationId);
        }

        OrganizationSettings organizationSettings = new OrganizationSettings(
                organization,
                request.businessDescription(),
                request.aiInstructions(),
                request.aiTone(),
                request.autoReplyEnabled(),
                request.requireHumanApproval(),
                request.settings());

        try {
            OrganizationSettings savedSettings = organizationSettingsRepository.saveAndFlush(organizationSettings);
            return toSettingsResponse(savedSettings);
        } catch (DataIntegrityViolationException exception) {
            throw new OrganizationSettingsConflictException(organizationId, exception);
        }
    }

    private OrganizationSettingsResponse toSettingsResponse(OrganizationSettings organizationSettings) {
        return new OrganizationSettingsResponse(
                organizationSettings.getId(),
                organizationSettings.getOrganization().getId(),
                organizationSettings.getBusinessDescription(),
                organizationSettings.getAiInstructions(),
                organizationSettings.getAiTone(),
                organizationSettings.isAutoReplyEnabled(),
                organizationSettings.isRequireHumanApproval(),
                organizationSettings.getSettings(),
                organizationSettings.getCreatedAt(),
                organizationSettings.getUpdatedAt());
    }

            @Override
            @Transactional(readOnly = true)
            public OrganizationBusinessInfo getOrganizationBusinessInfo(UUID organizationId) {
            Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new OrganizationNotFoundException(organizationId));
            OrganizationSettings settings = organizationSettingsRepository.findByOrganization_Id(organizationId)
                .orElseThrow(() -> new OrganizationBusinessSettingsRequiredException(organizationId));

            return new OrganizationBusinessInfo(
                organization.getId(),
                organization.getName(),
                organization.getEmail(),
                organization.getPhone(),
                organization.getAddress(),
                organization.getTimezone(),
                organization.getCurrency(),
                settings.getBusinessDescription(),
                settings.getAiInstructions(),
                settings.getAiTone() == null ? AITone.PROFESSIONAL : settings.getAiTone(),
                settings.isAutoReplyEnabled(),
                settings.isRequireHumanApproval(),
                settings.getSettings());
            }

    private String createUniqueSlug(String name) {
        String baseSlug = slugify(name);
        String slug = baseSlug;
        int suffix = 2;

        while (organizationRepository.existsBySlug(slug)) {
            String suffixText = "-" + suffix++;
            int baseLength = MAX_SLUG_LENGTH - suffixText.length();
            slug = baseSlug.substring(0, Math.min(baseSlug.length(), baseLength))
                    .replaceAll("-+$", "") + suffixText;
        }

        return slug;
    }

    private String slugify(String name) {
        String normalizedName = Normalizer.normalize(name, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
        String slug = normalizedName.replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+|-+$", "");

        if (slug.isEmpty()) {
            slug = "organization";
        }

        return slug.substring(0, Math.min(slug.length(), MAX_SLUG_LENGTH)).replaceAll("-+$", "");
    }
}