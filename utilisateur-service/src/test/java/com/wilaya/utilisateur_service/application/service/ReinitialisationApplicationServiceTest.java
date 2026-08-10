package com.wilaya.utilisateur_service.application.service;

import com.wilaya.utilisateur_service.domain.model.DemandeReinitialisation;
import com.wilaya.utilisateur_service.domain.model.ProfilUtilisateur;
import com.wilaya.utilisateur_service.domain.port.out.DemandeReinitialisationRepository;
import com.wilaya.utilisateur_service.domain.port.out.EmailSenderPort;
import com.wilaya.utilisateur_service.domain.port.out.IdentiteProviderPort;
import com.wilaya.utilisateur_service.domain.port.out.ProfilUtilisateurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReinitialisationApplicationServiceTest {

    @Mock
    private ProfilUtilisateurRepository profilRepository;

    @Mock
    private DemandeReinitialisationRepository demandeRepository;

    @Mock
    private IdentiteProviderPort identiteProvider;

    @Mock
    private EmailSenderPort emailSender;

    private ReinitialisationApplicationService service;

    private final UUID idKeycloak = UUID.randomUUID();
    private ProfilUtilisateur profilExistant;
    private static final String EMAIL = "karim@example.com";
    private static final String ROLE = "AGENT";

    @BeforeEach
    void setUp() {
        service = new ReinitialisationApplicationService(profilRepository, demandeRepository, identiteProvider, emailSender);
        profilExistant = new ProfilUtilisateur(idKeycloak, "Benali", "Karim", "0600000000", EMAIL, ROLE);
    }


    @Test
    void demanderReinitialisationAvecEmailExistantEtSousLaLimiteEnvoieUnCode() {
        when(profilRepository.findByEmail(EMAIL)).thenReturn(Optional.of(profilExistant));
        when(demandeRepository.compterDemandesRecentes(eq(idKeycloak), any(Duration.class))).thenReturn(0L);

        service.demanderReinitialisation(EMAIL);

        verify(demandeRepository).save(any(DemandeReinitialisation.class));
        verify(emailSender).envoyerCodeReinitialisation(eq(EMAIL), anyString());
    }

    @Test
    void demanderReinitialisationAvecEmailInexistantNeFaitRienEtNeLancePasDException() {
        when(profilRepository.findByEmail("inconnu@example.com")).thenReturn(Optional.empty());

        service.demanderReinitialisation("inconnu@example.com");

        verifyNoInteractions(demandeRepository);
        verifyNoInteractions(emailSender);
    }

    @Test
    void demanderReinitialisationAuDelaDeLaLimiteAntiSpamNeFaitRien() {
        when(profilRepository.findByEmail(EMAIL)).thenReturn(Optional.of(profilExistant));
        when(demandeRepository.compterDemandesRecentes(eq(idKeycloak), any(Duration.class))).thenReturn(3L);

        service.demanderReinitialisation(EMAIL);

        verify(demandeRepository, never()).save(any());
        verifyNoInteractions(emailSender);
    }

    @Test
    void demanderReinitialisationJusteSousLaLimiteEnvoieEncoreUnCode() {
        when(profilRepository.findByEmail(EMAIL)).thenReturn(Optional.of(profilExistant));
        when(demandeRepository.compterDemandesRecentes(eq(idKeycloak), any(Duration.class))).thenReturn(2L);

        service.demanderReinitialisation(EMAIL);

        verify(demandeRepository).save(any(DemandeReinitialisation.class));
        verify(emailSender).envoyerCodeReinitialisation(eq(EMAIL), anyString());
    }


    @Test
    void verifierEtReinitialiserAvecCodeValideChangeLeMotDePasse() {
        String code = "123456";
        DemandeReinitialisation demande = DemandeReinitialisation.creer(idKeycloak, code);
        when(profilRepository.findByEmail(EMAIL)).thenReturn(Optional.of(profilExistant));
        when(demandeRepository.findEnAttenteParUtilisateur(idKeycloak, code)).thenReturn(Optional.of(demande));

        service.verifierEtReinitialiser(EMAIL, code, "nouveauMdp789");

        verify(demandeRepository).save(demande);
        verify(identiteProvider).changerMotDePasse(idKeycloak, "nouveauMdp789", false);
    }

    @Test
    void verifierEtReinitialiserAvecEmailInconnuLanceIllegalArgumentException() {
        when(profilRepository.findByEmail("inconnu@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.verifierEtReinitialiser("inconnu@example.com", "123456", "nouveauMdp789"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Demande invalide");

        verifyNoInteractions(identiteProvider);
    }

    @Test
    void verifierEtReinitialiserSansDemandeEnAttenteLanceIllegalArgumentException() {
        when(profilRepository.findByEmail(EMAIL)).thenReturn(Optional.of(profilExistant));
        when(demandeRepository.findEnAttenteParUtilisateur(idKeycloak, "000000")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.verifierEtReinitialiser(EMAIL, "000000", "nouveauMdp789"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Code invalide");

        verifyNoInteractions(identiteProvider);
    }

    @Test
    void verifierEtReinitialiserAvecCodeIncorrectPropageLExceptionDuDomaine() {
        String bonCode = "123456";
        DemandeReinitialisation demande = DemandeReinitialisation.creer(idKeycloak, bonCode);
        when(profilRepository.findByEmail(EMAIL)).thenReturn(Optional.of(profilExistant));
        when(demandeRepository.findEnAttenteParUtilisateur(idKeycloak, "999999")).thenReturn(Optional.of(demande));

        assertThatThrownBy(() -> service.verifierEtReinitialiser(EMAIL, "999999", "nouveauMdp789"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Code invalide");

        verify(identiteProvider, never()).changerMotDePasse(any(), anyString(), anyBoolean());
    }

    @Test
    void verifierEtReinitialiserNAppellePasChangerMotDePasseSiVerifierCodeEchoue() {
        String code = "123456";
        DemandeReinitialisation demandeExpiree = DemandeReinitialisation.creer(idKeycloak, code);
        demandeExpiree.verifierCode(code);
        when(profilRepository.findByEmail(EMAIL)).thenReturn(Optional.of(profilExistant));
        when(demandeRepository.findEnAttenteParUtilisateur(idKeycloak, code)).thenReturn(Optional.of(demandeExpiree));

        assertThatThrownBy(() -> service.verifierEtReinitialiser(EMAIL, code, "nouveauMdp789"))
                .isInstanceOf(IllegalStateException.class);

        verifyNoInteractions(identiteProvider);
    }
}
