package com.wilaya.utilisateur_service.infrastructure.persistence;

import com.wilaya.utilisateur_service.domain.model.Agent;
import com.wilaya.utilisateur_service.domain.model.ProfilUtilisateur;
import com.wilaya.utilisateur_service.domain.model.StatutAgent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgentRepositoryAdapterTest {

    @Mock
    private AgentJpaRepository jpaRepository;

    @Mock
    private ProfilUtilisateurJpaRepository profilJpaRepository;

    @InjectMocks
    private AgentRepositoryAdapter agentRepositoryAdapter;

    private UUID idKeycloak;
    private UUID idEquipe;
    private ProfilUtilisateur profil;
    private Agent agent;

    @BeforeEach
    void setUp() {
        idKeycloak = UUID.randomUUID();
        idEquipe = UUID.randomUUID();
        profil = new ProfilUtilisateur(idKeycloak, "Alaoui", "Karim", "0612345678", "karim@example.com");
        agent = new Agent(profil, idEquipe);
    }

    @Test
    void save_devrait_persister_lentite_et_retourner_lagent_domaine() {
        when(jpaRepository.save(any(AgentJpaEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Agent resultat = agentRepositoryAdapter.save(agent);

        assertThat(resultat).isSameAs(agent);
        verify(jpaRepository, times(1)).save(argThat(entity ->
                entity.getIdProfil().equals(idKeycloak)
                        && entity.getIdEquipe().equals(idEquipe)
                        && entity.getStatut() == StatutAgent.ACTIF
        ));
    }

    @Test
    void findByIdEquipe_devrait_retourner_les_agents_reconstruits_avec_leur_profil() {
        AgentJpaEntity agentEntity = new AgentJpaEntity(idKeycloak, idEquipe, StatutAgent.ACTIF);
        ProfilUtilisateurJpaEntity profilEntity = new ProfilUtilisateurJpaEntity(
                idKeycloak, "Alaoui", "Karim", "0612345678", "karim@example.com", true);

        when(jpaRepository.findByIdEquipe(idEquipe)).thenReturn(List.of(agentEntity));
        when(profilJpaRepository.findById(idKeycloak)).thenReturn(Optional.of(profilEntity));

        List<Agent> resultat = agentRepositoryAdapter.findByIdEquipe(idEquipe);

        assertThat(resultat).hasSize(1);
        Agent agentTrouve = resultat.get(0);
        assertThat(agentTrouve.getIdEquipe()).isEqualTo(idEquipe);
        assertThat(agentTrouve.getProfil().getIdKeycloak()).isEqualTo(idKeycloak);
        assertThat(agentTrouve.getProfil().getNom()).isEqualTo("Alaoui");
        assertThat(agentTrouve.getStatut()).isEqualTo(StatutAgent.ACTIF);
    }

    @Test
    void findByIdEquipe_devrait_retourner_liste_vide_si_aucun_agent() {
        when(jpaRepository.findByIdEquipe(idEquipe)).thenReturn(List.of());

        List<Agent> resultat = agentRepositoryAdapter.findByIdEquipe(idEquipe);

        assertThat(resultat).isEmpty();
        verifyNoInteractions(profilJpaRepository);
    }

    @Test
    void findByIdEquipe_devrait_lever_exception_si_profil_introuvable() {
        AgentJpaEntity agentEntity = new AgentJpaEntity(idKeycloak, idEquipe, StatutAgent.ACTIF);

        when(jpaRepository.findByIdEquipe(idEquipe)).thenReturn(List.of(agentEntity));
        when(profilJpaRepository.findById(idKeycloak)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> agentRepositoryAdapter.findByIdEquipe(idEquipe))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Profil introuvable pour l'agent " + idKeycloak);
    }
}