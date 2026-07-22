package com.wilaya.signalement_service.service;

import com.wilaya.signalement_service.dto.CreerSignalementRequest;
import com.wilaya.signalement_service.exception.DoublonSignalementException;
import com.wilaya.signalement_service.messaging.SignalementEventPublisher;
import com.wilaya.signalement_service.model.*;
import com.wilaya.signalement_service.policy.PolicyCalculGravite;
import com.wilaya.signalement_service.policy.ValidateurCIN;
import com.wilaya.signalement_service.repository.SignalementRepository;
import com.wilaya.signalement_service.storage.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SignalementServiceTest {

    @Mock private SignalementRepository repository;
    @Mock private PolicyCalculGravite policyCalculGravite;
    @Mock private ValidateurCIN validateurCIN;
    @Mock private SignalementEventPublisher eventPublisher;
    @Mock private FileStorageService fileStorageService;

    @InjectMocks
    private SignalementService signalementService;

    private CreerSignalementRequest request;

    @BeforeEach
    void setUp() {
        request = new CreerSignalementRequest(
                "AB123456", TypeIntervention.VOIRIE, "Nid de poule", "Zone A", "Rue Principale"
        );
    }

    @Test
    @DisplayName("Devrait créer un signalement avec succès si valide")
    void creerSignalement_Succes() {
        // Given
        when(validateurCIN.estValide(anyString())).thenReturn(true);
        when(repository.findByTypeAndZoneAndAdresseAndDateCreationAfterAndStatutNotIn(
                any(), any(), any(), any(), any())).thenReturn(Collections.emptyList());
        when(repository.findByCinDeclarantAndDateCreationAfterAndStatutNotIn(
                any(), any(), any())).thenReturn(Collections.emptyList());

        when(policyCalculGravite.calculer(any(), any())).thenReturn(NiveauGravite.MOYENNE);

        // Mock de sauvegarde pour retourner le signalement
        when(repository.save(any(Signalement.class))).thenAnswer(i -> i.getArguments()[0]);

        // When
        Signalement result = signalementService.creerSignalement(request, null);

        // Then
        assertNotNull(result);
        assertEquals("AB123456", result.getCinDeclarant());
        assertEquals(StatutSignalement.CLASSIFIE, result.getStatut());
        verify(eventPublisher, times(1)).publierSignalementClassifie(any());
    }

    @Test
    @DisplayName("Devrait lever DoublonSignalementException si un doublon existe")
    void creerSignalement_DoublonExistant() {
        // Given
        when(validateurCIN.estValide(anyString())).thenReturn(true);

        // Simuler un doublon trouvé dans le repository
        Signalement doublon = new Signalement("AB123456", TypeIntervention.VOIRIE, "Desc", "url", "Zone A", NiveauGravite.BASSE, "Rue Principale");
        when(repository.findByTypeAndZoneAndAdresseAndDateCreationAfterAndStatutNotIn(
                any(), any(), any(), any(), any())).thenReturn(Collections.singletonList(doublon));

        // When & Then
        assertThrows(DoublonSignalementException.class, () -> {
            signalementService.creerSignalement(request, null);
        });
    }
}