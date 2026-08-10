package com.wilaya.utilisateur_service.api.dto;

import com.wilaya.utilisateur_service.domain.model.Agent;
import com.wilaya.utilisateur_service.domain.model.ProfilUtilisateur;
import com.wilaya.utilisateur_service.domain.model.StatutAgent;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AgentResponseTest {

    private static final String ROLE = "AGENT";

    @Test
    void depuisMappeCorrectementUnAgentActif() {
        ProfilUtilisateur profil = new ProfilUtilisateur(
                UUID.randomUUID(), "Benali", "Karim", "0600000000", "karim@example.com", ROLE
        );
        UUID idEquipe = UUID.randomUUID();
        Agent agent = new Agent(profil, idEquipe);

        AgentResponse response = AgentResponse.depuis(agent);

        assertThat(response.idProfil()).isEqualTo(profil.getIdKeycloak());
        assertThat(response.idEquipe()).isEqualTo(idEquipe);
        assertThat(response.statut()).isEqualTo("ACTIF");
    }

    @Test
    void depuisMappeCorrectementUnAgentInactif() {
        ProfilUtilisateur profil = new ProfilUtilisateur(
                UUID.randomUUID(), "Alaoui", "Yassine", "0611111111", "yassine@example.com", ROLE
        );
        Agent agent = new Agent(profil, UUID.randomUUID());
        agent.desactiver();

        AgentResponse response = AgentResponse.depuis(agent);

        assertThat(response.statut()).isEqualTo(StatutAgent.INACTIF.name());
    }

    @Test
    void depuisRefleteUneReaffectationDEquipe() {
        ProfilUtilisateur profil = new ProfilUtilisateur(
                UUID.randomUUID(), "Benali", "Karim", "0600000000", "karim@example.com", ROLE
        );
        Agent agent = new Agent(profil, UUID.randomUUID());
        UUID nouvelleEquipe = UUID.randomUUID();
        agent.reaffecterEquipe(nouvelleEquipe);

        AgentResponse response = AgentResponse.depuis(agent);

        assertThat(response.idEquipe()).isEqualTo(nouvelleEquipe);
    }
}







































