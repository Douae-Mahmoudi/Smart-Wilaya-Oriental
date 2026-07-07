package com.wilaya.utilisateur_service.application.service;

import com.wilaya.utilisateur_service.domain.model.ProfilUtilisateur;
import com.wilaya.utilisateur_service.domain.port.out.EmailSenderPort;
import com.wilaya.utilisateur_service.domain.port.out.IdentiteProviderPort;
import com.wilaya.utilisateur_service.domain.port.out.ProfilUtilisateurRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompteApplicationServiceTest {

    @Mock
    private ProfilUtilisateurRepository profilRepository;

    @Mock
    private IdentiteProviderPort identiteProvider;

    @Mock
    private EmailSenderPort emailSender;

    private CompteApplicationService service;

    private final UUID idKeycloakGenere = UUID.randomUUID();

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        service = new CompteApplicationService(profilRepository, identiteProvider, emailSender);
    }

    @Test
    void creerCompteAppelleIdentiteProviderAvecLesBonnesInformations() {
        when(identiteProvider.creerCompte(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(idKeycloakGenere);

        service.creerCompte("Benali", "Karim", "karim@example.com", "0600000000", "AGENT", null);

        verify(identiteProvider).creerCompte(
                eq("karim@example.com"), eq("Benali"), eq("Karim"), anyString(), eq("AGENT")
        );
    }

    @Test
    void creerCompteGenereUnMotDePasseTemporaireDe12Caracteres() {
        when(identiteProvider.creerCompte(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(idKeycloakGenere);
        ArgumentCaptor<String> motDePasseCaptor = ArgumentCaptor.forClass(String.class);

        service.creerCompte("Benali", "Karim", "karim@example.com", "0600000000", "AGENT", null);

        verify(identiteProvider).creerCompte(anyString(), anyString(), anyString(), motDePasseCaptor.capture(), anyString());
        assertThat(motDePasseCaptor.getValue()).hasSize(12);
    }

    @Test
    void creerCompteSauvegardeLeProfilAvecLIdKeycloakRetourne() {
        when(identiteProvider.creerCompte(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(idKeycloakGenere);
        ArgumentCaptor<ProfilUtilisateur> profilCaptor = ArgumentCaptor.forClass(ProfilUtilisateur.class);

        service.creerCompte("Benali", "Karim", "karim@example.com", "0600000000", "AGENT", null);

        verify(profilRepository).save(profilCaptor.capture());
        ProfilUtilisateur profilSauvegarde = profilCaptor.getValue();
        assertThat(profilSauvegarde.getIdKeycloak()).isEqualTo(idKeycloakGenere);
        assertThat(profilSauvegarde.getEmail()).isEqualTo("karim@example.com");
        assertThat(profilSauvegarde.getNom()).isEqualTo("Benali");
    }

    @Test
    void creerCompteEnvoieUnEmailAvecLesIdentifiantsTemporaires() {
        when(identiteProvider.creerCompte(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(idKeycloakGenere);
        ArgumentCaptor<String> motDePasseCaptor = ArgumentCaptor.forClass(String.class);

        service.creerCompte("Benali", "Karim", "karim@example.com", "0600000000", "AGENT", null);

        verify(emailSender).envoyerIdentifiantsTemporaires(
                eq("karim@example.com"), eq("karim@example.com"), motDePasseCaptor.capture()
        );
        assertThat(motDePasseCaptor.getValue()).hasSize(12);
    }

    @Test
    void creerCompteRenvoieLIdKeycloakGenereParLIdentiteProvider() {
        when(identiteProvider.creerCompte(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(idKeycloakGenere);

        UUID resultat = service.creerCompte("Benali", "Karim", "karim@example.com", "0600000000", "AGENT", null);

        assertThat(resultat).isEqualTo(idKeycloakGenere);
    }

    @Test
    void creerCompteAvecRoleSuperviseurEtIdEquipeNullNeLancePasDException() {
        when(identiteProvider.creerCompte(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(idKeycloakGenere);

        UUID resultat = service.creerCompte("Alaoui", "Yassine", "yassine@example.com", "0611111111", "SUPERVISEUR", null);

        assertThat(resultat).isEqualTo(idKeycloakGenere);
    }


    @Test
    void creerCompteAvecRoleAgentEtIdEquipeNeSauvegardePasLAgentActuellement() {
        when(identiteProvider.creerCompte(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(idKeycloakGenere);
        UUID idEquipe = UUID.randomUUID();

        UUID resultat = service.creerCompte("Benali", "Karim", "karim@example.com", "0600000000", "AGENT", idEquipe);

        assertThat(resultat).isEqualTo(idKeycloakGenere);
        verify(profilRepository, times(1)).save(any());
    }
}
