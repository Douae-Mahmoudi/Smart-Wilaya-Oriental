package com.wilaya.signalement_service.service;

import com.wilaya.signalement_service.dto.CreerSignalementRequest;
import com.wilaya.signalement_service.exception.RessourceNonTrouveeException;
import com.wilaya.signalement_service.messaging.SignalementEventPublisher;
import com.wilaya.signalement_service.model.*;
import com.wilaya.signalement_service.policy.PolicyCalculGravite;
import com.wilaya.signalement_service.policy.ValidateurCIN;
import com.wilaya.signalement_service.repository.SignalementRepository;
import com.wilaya.signalement_service.storage.FileStorageService;
import com.wilaya.signalement_service.util.TfIdfVectorizer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SignalementServiceTest {

    @Mock private SignalementRepository repository;
    @Mock private PolicyCalculGravite policyCalculGravite;
    @Mock private ValidateurCIN validateurCIN;
    @Mock private SignalementEventPublisher eventPublisher;
    @Mock private FileStorageService fileStorageService;
    @Mock private TextSimilarityService textSimilarityService;
    @Mock private TfIdfVectorizer vectorizer;

    @InjectMocks
    private SignalementService service;

    private final MockMultipartFile photo = new MockMultipartFile("photo", "photo.jpg", "image/jpeg", new byte[]{1, 2, 3});

    @Test
    void devrait_creer_un_signalement_valide() {
        CreerSignalementRequest request = new CreerSignalementRequest(
                "AB123456", TypeIntervention.EAU, "Fuite importante", "Zone Nord", "Rue Test", 34.68, -1.90);

        when(validateurCIN.estValide("AB123456")).thenReturn(true);
        when(policyCalculGravite.calculer(TypeIntervention.EAU, "Zone Nord")).thenReturn(NiveauGravite.HAUTE);
        when(fileStorageService.sauvegarder(photo)).thenReturn("photo-url.jpg");
        when(repository.save(any(Signalement.class))).thenAnswer(i -> i.getArgument(0));

        Signalement resultat = service.creerSignalement(request, photo);

        assertThat(resultat.getGravite()).isEqualTo(NiveauGravite.HAUTE);
        assertThat(resultat.getCinDeclarant()).isEqualTo("AB123456");
        verify(repository).save(any(Signalement.class));
    }

    @Test
    void devrait_rejeter_un_cin_invalide() {
        CreerSignalementRequest request = new CreerSignalementRequest(
                "INVALIDE", TypeIntervention.EAU, "Fuite", "Zone Nord", "Rue Test", 34.68, -1.90);

        when(validateurCIN.estValide("INVALIDE")).thenReturn(false);

        assertThatThrownBy(() -> service.creerSignalement(request, photo))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("CIN");

        verify(repository, never()).save(any());
    }

    @Test
    void devrait_trouver_un_signalement_par_numero_suivi() {
        Signalement signalement = new Signalement("AB123456", TypeIntervention.EAU, "desc", "photo.jpg", "Zone Nord", NiveauGravite.MOYENNE, "Rue Test", 34.68, -1.90);
        when(repository.findByNumeroSuivi("SIG-1234")).thenReturn(Optional.of(signalement));

        Signalement resultat = service.trouverParNumeroSuivi("SIG-1234");

        assertThat(resultat).isEqualTo(signalement);
    }

    @Test
    void devrait_lever_exception_si_numero_suivi_inconnu() {
        when(repository.findByNumeroSuivi("SIG-INCONNU")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.trouverParNumeroSuivi("SIG-INCONNU"))
                .isInstanceOf(RessourceNonTrouveeException.class);
    }

    @Test
    void devrait_changer_le_statut_et_publier_evenement_si_classifie() {
        UUID id = UUID.randomUUID();
        Signalement signalement = new Signalement("AB123456", TypeIntervention.EAU, "desc", "photo.jpg", "Zone Nord", NiveauGravite.MOYENNE, "Rue Test", 34.68, -1.90);
        when(repository.findById(id)).thenReturn(Optional.of(signalement));
        when(repository.save(any(Signalement.class))).thenAnswer(i -> i.getArgument(0));

        service.changerStatut(id, StatutSignalement.CLASSIFIE, "Classifié pour test");

        verify(eventPublisher).publierSignalementClassifie(any(Signalement.class));
    }

    @Test
    void devrait_lever_exception_si_id_inconnu_lors_du_changement_statut() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.changerStatut(id, StatutSignalement.CLASSIFIE, "Classifié pour test"))
                .isInstanceOf(RessourceNonTrouveeException.class);
    }

    @Test
    void devrait_lister_tous_les_signalements() {
        when(repository.findAll()).thenReturn(List.of(new Signalement("AB123456", TypeIntervention.EAU, "desc", "photo.jpg", "Zone Nord", NiveauGravite.MOYENNE, "Rue Test", 34.68, -1.90)));

        List<Signalement> resultat = service.listerTout();

        assertThat(resultat).hasSize(1);
    }
}
