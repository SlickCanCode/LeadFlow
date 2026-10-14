package com.slick.lead_revenue_automation_system.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.util.ReflectionTestUtils;

import com.slick.lead_revenue_automation_system.dto.CreateOrganizationdto;
import com.slick.lead_revenue_automation_system.dto.CreateOrganizationSettingsRequest;
import com.slick.lead_revenue_automation_system.dto.OrganizationBusinessInfo;
import com.slick.lead_revenue_automation_system.entities.Organization;
import com.slick.lead_revenue_automation_system.entities.OrganizationSettings;
import com.slick.lead_revenue_automation_system.entities.enums.AITone;
import com.slick.lead_revenue_automation_system.exception.OrganizationConflictException;
import com.slick.lead_revenue_automation_system.exception.OrganizationBusinessSettingsRequiredException;
import com.slick.lead_revenue_automation_system.exception.OrganizationNotFoundException;
import com.slick.lead_revenue_automation_system.exception.OrganizationSettingsConflictException;
import com.slick.lead_revenue_automation_system.repositories.OrganizationRepository;
import com.slick.lead_revenue_automation_system.repositories.OrganizationSettingsRepository;

@ExtendWith(MockitoExtension.class)
class OrganizationServiceImplTest {
    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private OrganizationSettingsRepository organizationSettingsRepository;

    @InjectMocks
    private OrganizationServiceImpl organizationService;

    @Test
    void createsOrganizationFromNameAndAddsSuffixWhenSlugExists() {
        when(organizationRepository.existsBySlug("acme-services")).thenReturn(true);
        when(organizationRepository.existsBySlug("acme-services-2")).thenReturn(false);
        when(organizationRepository.saveAndFlush(any(Organization.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        var response = organizationService.createOrganization(
                new CreateOrganizationdto(" Acme Services ", null, null, null, null, null, null));

        ArgumentCaptor<Organization> organizationCaptor = ArgumentCaptor.forClass(Organization.class);
        verify(organizationRepository).saveAndFlush(organizationCaptor.capture());
        Organization savedOrganization = organizationCaptor.getValue();
        assertEquals("Acme Services", savedOrganization.getName());
        assertEquals("acme-services-2", savedOrganization.getSlug());
        assertEquals("UTC", savedOrganization.getTimezone());
        assertEquals("USD", savedOrganization.getCurrency());
        assertEquals("acme-services-2", response.slug());
        verify(organizationRepository, never()).existsBySlug("acme-services-3");
    }

    @Test
    void translatesDatabaseSlugCollisionIntoOrganizationConflict() {
        when(organizationRepository.existsBySlug("acme-services")).thenReturn(false);
        when(organizationRepository.saveAndFlush(any(Organization.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate slug"));

        assertThrows(OrganizationConflictException.class, () -> organizationService.createOrganization(
                new CreateOrganizationdto("Acme Services", null, null, null, null, null, null)));
    }

            @Test
            void createsSettingsUsingDefaultsWhenOrganizationExists() {
            UUID organizationId = UUID.fromString("53c13798-92d1-4f0a-9f9a-9a20f7153592");
            Organization organization = new Organization("Acme Services", "acme-services", null, null, null,
                null, null, null);
            ReflectionTestUtils.setField(organization, "id", organizationId);
            when(organizationRepository.findById(organizationId)).thenReturn(Optional.of(organization));
            when(organizationSettingsRepository.existsByOrganization_Id(organizationId)).thenReturn(false);
            when(organizationSettingsRepository.saveAndFlush(any(OrganizationSettings.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

            var response = organizationService.createOrganizationSettings(organizationId,
                new CreateOrganizationSettingsRequest(null, null, null, null, null, null));

            ArgumentCaptor<OrganizationSettings> settingsCaptor = ArgumentCaptor.forClass(OrganizationSettings.class);
            verify(organizationSettingsRepository).saveAndFlush(settingsCaptor.capture());
            OrganizationSettings savedSettings = settingsCaptor.getValue();
            assertEquals(AITone.PROFESSIONAL, savedSettings.getAiTone());
            assertEquals(false, savedSettings.isAutoReplyEnabled());
            assertEquals(true, savedSettings.isRequireHumanApproval());
            assertEquals(Map.of(), savedSettings.getSettings());
            assertSame(organization, savedSettings.getOrganization());
            assertEquals(organizationId, response.organizationId());
            }

            @Test
            void rejectsSettingsForUnknownOrganization() {
            UUID organizationId = UUID.fromString("53c13798-92d1-4f0a-9f9a-9a20f7153592");
            when(organizationRepository.findById(organizationId)).thenReturn(Optional.empty());

            assertThrows(OrganizationNotFoundException.class, () -> organizationService.createOrganizationSettings(
                organizationId, new CreateOrganizationSettingsRequest(null, null, null, null, null, null)));

            verifyNoInteractions(organizationSettingsRepository);
            }

            @Test
            void rejectsSecondSettingsRecordForOrganization() {
            UUID organizationId = UUID.fromString("53c13798-92d1-4f0a-9f9a-9a20f7153592");
            Organization organization = new Organization("Acme Services", "acme-services", null, null, null,
                null, null, null);
            when(organizationRepository.findById(organizationId)).thenReturn(Optional.of(organization));
            when(organizationSettingsRepository.existsByOrganization_Id(organizationId)).thenReturn(true);

            assertThrows(OrganizationSettingsConflictException.class,
                () -> organizationService.createOrganizationSettings(organizationId,
                    new CreateOrganizationSettingsRequest(null, null, null, null, null, null)));

            verify(organizationSettingsRepository, never()).saveAndFlush(any(OrganizationSettings.class));
            }

            @Test
            void retrievesOrganizationBusinessInfoFromOrganizationAndSettings() {
            UUID organizationId = UUID.fromString("53c13798-92d1-4f0a-9f9a-9a20f7153592");
            Organization organization = new Organization("Acme Services", "acme-services", "help@acme.test",
                "555-0100", "Main Street", null, "UTC", "USD");
            ReflectionTestUtils.setField(organization, "id", organizationId);
            OrganizationSettings settings = new OrganizationSettings(organization, "Home repairs", "Be friendly",
                AITone.FRIENDLY, false, true, Map.of("category", "repairs"));
            when(organizationRepository.findById(organizationId)).thenReturn(Optional.of(organization));
            when(organizationSettingsRepository.findByOrganization_Id(organizationId)).thenReturn(Optional.of(settings));

            OrganizationBusinessInfo info = organizationService.getOrganizationBusinessInfo(organizationId);

            assertEquals("Acme Services", info.name());
            assertEquals("help@acme.test", info.email());
            assertEquals("Home repairs", info.businessDescription());
            assertEquals(AITone.FRIENDLY, info.aiTone());
            assertEquals(Map.of("category", "repairs"), info.settings());
            }

            @Test
            void requiresBusinessSettingsBeforeRetrievingAssistantContext() {
            UUID organizationId = UUID.fromString("53c13798-92d1-4f0a-9f9a-9a20f7153592");
            Organization organization = new Organization("Acme Services", "acme-services", null, null, null,
                null, null, null);
            when(organizationRepository.findById(organizationId)).thenReturn(Optional.of(organization));
            when(organizationSettingsRepository.findByOrganization_Id(organizationId)).thenReturn(Optional.empty());

            assertThrows(OrganizationBusinessSettingsRequiredException.class,
                () -> organizationService.getOrganizationBusinessInfo(organizationId));
            }
}