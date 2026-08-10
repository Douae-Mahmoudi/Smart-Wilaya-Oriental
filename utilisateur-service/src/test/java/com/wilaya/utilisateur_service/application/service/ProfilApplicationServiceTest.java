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
        profilExistant = new ProfilUtilisateur(idKeycloak, "Benali", "Karim", "0600000000", "karim@example.com", "AGENT");
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


    @Test
    void listerTousDelegueAuAgentRepositoryEtRenvoieTousLesAgents() {
        UUID idEquipe1 = UUID.randomUUID();
        UUID idEquipe2 = UUID.randomUUID();
        Agent agent1 = new Agent(profilExistant, idEquipe1);
        Agent agent2 = new Agent(
                new ProfilUtilisateur(UUID.randomUUID(), "Dupont", "Jean", "0622222222", "jean@example.com", "AGENT"),
                idEquipe2
        );
        List<Agent> agents = List.of(agent1, agent2);
        when(agentRepository.findAll()).thenReturn(agents);

        List<Agent> resultat = service.listerTous();

        assertThat(resultat).containsExactly(agent1, agent2);
        verify(agentRepository).findAll();
    }

    @Test
    void listerTousRenvoieUneListeVideSiAucunAgent() {
        when(agentRepository.findAll()).thenReturn(List.of());

        List<Agent> resultat = service.listerTous();

        assertThat(resultat).isEmpty();
    }


    @Test
    void obtenirIdEquipeRetourneLIdEquipeSiAgentTrouve() {
        UUID idEquipe = UUID.randomUUID();
        Agent agent = new Agent(profilExistant, idEquipe);
        when(agentRepository.findByIdProfil(idKeycloak)).thenReturn(Optional.of(agent));

        UUID resultat = service.obtenirIdEquipe(idKeycloak);

        assertThat(resultat).isEqualTo(idEquipe);
        verify(agentRepository).findByIdProfil(idKeycloak);
    }

    @Test
    void obtenirIdEquipeLanceNoSuchElementExceptionSiAucunAgentTrouve() {
        when(agentRepository.findByIdProfil(idKeycloak)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obtenirIdEquipe(idKeycloak))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Aucune équipe associée à cet agent");

        verify(agentRepository).findByIdProfil(idKeycloak);
    }
}