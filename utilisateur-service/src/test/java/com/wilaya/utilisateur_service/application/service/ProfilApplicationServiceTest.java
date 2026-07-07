package com.wilaya.utilisateur_service.application.service;

import com.wilaya.utilisateur_service.domain.model.Agent;
import com.wilaya.utilisateur_service.domain.model.ProfilUtilisateur;
import com.wilaya.utilisateur_service.domain.port.out.AgentRepository;
import com.wilaya.utilisateur_service.domain.port.out.IdentiteProviderPort;
import com.wilaya.utilisateur_service.domain.port.out.ProfilUtilisateurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfilApplicationServiceTest {

    @Mock
    private ProfilUtilisateurRepository profilRepository;

    @Mock
    private IdentiteProviderPort identiteProviderPort;

    @Mock
    private AgentRepository agentRepository;

    private ProfilApplicationService service;

    private final UUID idKeycloak = UUID.randomUUID();
    private ProfilUtilisateur profilExistant;

    @BeforeEach
    void setUp() {
        service = new ProfilApplicationService(profilRepository, identiteProviderPort, agentRepository);
        profilExistant = new ProfilUtilisateur(idKeycloak, "Benali", "Karim", "0600000000", "karim@example.com");
    }


    @Test
    void modifierProfilMetAJourLeProfilExistantEtLeSauvegarde() {
        when(profilRepository.findByIdKeycloak(idKeycloak)).thenReturn(Optional.of(profilExistant));

        service.modifierProfil(idKeycloak, "Alaoui", "Yassine", "0611111111", true);

        assertThat(profilExistant.getNom()).isEqualTo("Alaoui");
        assertThat(profilExistant.getPrenom()).isEqualTo("Yassine");
        assertThat(profilExistant.getTelephone()).isEqualTo("0611111111");
        assertThat(profilExistant.isNotificationsActivees()).isTrue();
        verify(profilRepository).save(profilExistant);
    }

    @Test
    void modifierProfilAvecNotificationsActiveesFalseDesactiveLesNotifications() {
        when(profilRepository.findByIdKeycloak(idKeycloak)).thenReturn(Optional.of(profilExistant));

        service.modifierProfil(idKeycloak, "Alaoui", "Yassine", "0611111111", false);

        assertThat(profilExistant.isNotificationsActivees()).isFalse();
    }

    @Test
    void modifierProfilSurUnProfilInexistantLanceNoSuchElementException() {
        when(profilRepository.findByIdKeycloak(idKeycloak)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.modifierProfil(idKeycloak, "Alaoui", "Yassine", "0611111111", true))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Profil introuvable");

        verify(profilRepository, never()).save(any());
    }


    @Test
    void changerMotDePasseAvecAncienMotDePasseValideAppelleLeChangement() {
        when(identiteProviderPort.verifierAncienMotDePasse(idKeycloak, "ancienMdp123")).thenReturn(true);

        service.changerMotDePasse(idKeycloak, "ancienMdp123", "nouveauMdp456");

        verify(identiteProviderPort).changerMotDePasse(idKeycloak, "nouveauMdp456", false);
    }

    @Test
    void changerMotDePasseAvecAncienMotDePasseInvalideLanceIllegalArgumentException() {
        when(identiteProviderPort.verifierAncienMotDePasse(idKeycloak, "mauvaisMdp")).thenReturn(false);

        assertThatThrownBy(() -> service.changerMotDePasse(idKeycloak, "mauvaisMdp", "nouveauMdp456"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Ancien mot de passe incorrect");

        verify(identiteProviderPort, never()).changerMotDePasse(any(), anyString(), anyBoolean());
    }


    @Test
    void listerParEquipeDelegueAuAgentRepositoryEtRenvoieSonResultat() {
        UUID idEquipe = UUID.randomUUID();
        Agent agent = new Agent(profilExistant, idEquipe);
        when(agentRepository.findByIdEquipe(idEquipe)).thenReturn(List.of(agent));

        List<Agent> resultat = service.listerParEquipe(idEquipe);

        assertThat(resultat).containsExactly(agent);
        verify(agentRepository).findByIdEquipe(idEquipe);
    }

    @Test
    void listerParEquipeRenvoieUneListeVideSiAucunAgentTrouve() {
        UUID idEquipe = UUID.randomUUID();
        when(agentRepository.findByIdEquipe(idEquipe)).thenReturn(List.of());

        List<Agent> resultat = service.listerParEquipe(idEquipe);

        assertThat(resultat).isEmpty();
    }
}















