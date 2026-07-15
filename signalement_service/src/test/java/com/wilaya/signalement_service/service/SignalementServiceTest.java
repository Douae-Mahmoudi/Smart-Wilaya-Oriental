package com.wilaya.signalement_service.service;

import com.wilaya.signalement_service.dto.CreerSignalementRequest;
import com.wilaya.signalement_service.exception.DoublonSignalementException;
import com.wilaya.signalement_service.exception.RessourceNonTrouveeException;
import com.wilaya.signalement_service.messaging.SignalementEventPublisher;
import com.wilaya.signalement_service.model.*;
import com.wilaya.signalement_service.policy.PolicyCalculGravite;
import com.wilaya.signalement_service.policy.ValidateurCIN;
import com.wilaya.signalement_service.repository.SignalementRepository;
import com.wilaya.signalement_service.storage.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SignalementServiceTest {

    @Mock private SignalementRepository repository;
    @Mock private PolicyCalculGravite policyCalculGravite;
    @Mock private ValidateurCIN validateurCIN;
    @Mock private SignalementEventPublisher eventPublisher;
    @Mock private FileStorageService fileStorageService;
    @Mock private MultipartFile photo;

    @InjectMocks private SignalementService signalementService;

    private CreerSignalementRequest request;

    @BeforeEach
    void setUp() {
        request = new CreerSignalementRequest("AB123456", TypeIntervention.VOIRIE, "Desc", "Zone");
    }

    @Test
    void testCreerSignalementSucces() {
        when(validateurCIN.estValide(anyString())).thenReturn(true);
        when(repository.findByTypeAndZoneAndDateCreationAfterAndStatutNotIn(any(), anyString(), any(), any())).thenReturn(List.of());
        when(repository.findByCinDeclarantAndDateCreationAfterAndStatutNotIn(anyString(), any(), any())).thenReturn(List.of());
        when(policyCalculGravite.calculer(any(), anyString())).thenReturn(NiveauGravite.MOYENNE);
        when(repository.save(any(Signalement.class))).thenAnswer(i -> i.getArguments()[0]);

        Signalement result = signalementService.creerSignalement(request, photo);

        assertNotNull(result);
        verify(repository).save(any(Signalement.class));
    }

    @Test
    void testCreerSignalementDoublonException() {
        when(validateurCIN.estValide(anyString())).thenReturn(true);
        Signalement existing = new Signalement("AB123456", TypeIntervention.VOIRIE, "desc", "url", "Zone", NiveauGravite.BASSE);
        when(repository.findByTypeAndZoneAndDateCreationAfterAndStatutNotIn(any(), anyString(), any(), any())).thenReturn(List.of(existing));

        assertThrows(DoublonSignalementException.class, () -> signalementService.creerSignalement(request, photo));
    }

    @Test
    void testChangerStatutSucces() {
        UUID id = UUID.randomUUID();
        Signalement s = new Signalement("AB123", TypeIntervention.VOIRIE, "d", "u", "z", NiveauGravite.BASSE);
        when(repository.findById(id)).thenReturn(Optional.of(s));
        when(repository.save(any(Signalement.class))).thenReturn(s);

        Signalement result = signalementService.changerStatut(id, StatutSignalement.CLASSIFIE);

        assertEquals(StatutSignalement.CLASSIFIE, result.getStatut());
        verify(eventPublisher).publierSignalementClassifie(any());
    }

    @Test
    void testTrouverParNumeroSuiviNonTrouve() {
        when(repository.findByNumeroSuivi("NONE")).thenReturn(Optional.empty());
        assertThrows(RessourceNonTrouveeException.class, () -> signalementService.trouverParNumeroSuivi("NONE"));
    }
}