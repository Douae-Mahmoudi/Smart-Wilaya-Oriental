package com.wilaya.utilisateur_service.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AgentTest {

    private ProfilUtilisateur profil;
    private UUID idEquipeInitiale;
    private Agent agent;

    @BeforeEach
    void setUp() {
        profil = new ProfilUtilisateur(
                UUID.randomUUID(),
                "Benali",
                "Karim",
                "0600000000",
                "karim.benali@example.com",
                "AGENT"
        );
        idEquipeInitiale = UUID.randomUUID();
        agent = new Agent(profil, idEquipeInitiale);
    }

    @Test
    void unNouvelAgentEstActifParDefaut() {
        assertThat(agent.getStatut()).isEqualTo(StatutAgent.ACTIF);
    }

    @Test
    void unNouvelAgentConserveLeProfilEtLEquipeFournis() {
        assertThat(agent.getProfil()).isEqualTo(profil);
        assertThat(agent.getIdEquipe()).isEqualTo(idEquipeInitiale);
    }

    @Test
    void desactiverPasseLeStatutAInactif() {
        agent.desactiver();

        assertThat(agent.getStatut()).isEqualTo(StatutAgent.INACTIF);
    }

    @Test
    void reactiverPasseLeStatutAActif() {
        agent.desactiver();

        agent.reactiver();

        assertThat(agent.getStatut()).isEqualTo(StatutAgent.ACTIF);
    }

    @Test
    void reactiverUnAgentDejaActifResteActif() {
        agent.reactiver();

        assertThat(agent.getStatut()).isEqualTo(StatutAgent.ACTIF);
    }

    @Test
    void reaffecterEquipeMetAJourLIdEquipe() {
        UUID nouvelleEquipe = UUID.randomUUID();

        agent.reaffecterEquipe(nouvelleEquipe);

        assertThat(agent.getIdEquipe()).isEqualTo(nouvelleEquipe);
        assertThat(agent.getIdEquipe()).isNotEqualTo(idEquipeInitiale);
    }

    @Test
    void reaffecterEquipeNeChangePasLeStatut() {
        agent.reaffecterEquipe(UUID.randomUUID());

        assertThat(agent.getStatut()).isEqualTo(StatutAgent.ACTIF);
    }
}











































