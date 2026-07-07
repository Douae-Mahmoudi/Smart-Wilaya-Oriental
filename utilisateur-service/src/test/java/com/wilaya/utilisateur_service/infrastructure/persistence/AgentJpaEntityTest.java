package com.wilaya.utilisateur_service.infrastructure.persistence;

import com.wilaya.utilisateur_service.domain.model.StatutAgent;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AgentJpaEntityTest {

    @Test
    void constructeur_doitInitialiserTousLesChamps() {
        UUID idProfil = UUID.randomUUID();
        UUID idEquipe = UUID.randomUUID();
        StatutAgent statut = StatutAgent.ACTIF;

        AgentJpaEntity agent = new AgentJpaEntity(idProfil, idEquipe, statut);

        assertEquals(idProfil, agent.getIdProfil());
        assertEquals(idEquipe, agent.getIdEquipe());
        assertEquals(statut, agent.getStatut());
    }

    @Test
    void setIdProfil_doitModifierLaValeur() {
        AgentJpaEntity agent = new AgentJpaEntity(UUID.randomUUID(), UUID.randomUUID(), StatutAgent.ACTIF);
        UUID nouveauId = UUID.randomUUID();

        agent.setIdProfil(nouveauId);

        assertEquals(nouveauId, agent.getIdProfil());
    }

    @Test
    void setIdEquipe_doitModifierLaValeur() {
        AgentJpaEntity agent = new AgentJpaEntity(UUID.randomUUID(), UUID.randomUUID(), StatutAgent.ACTIF);
        UUID nouvelleEquipe = UUID.randomUUID();

        agent.setIdEquipe(nouvelleEquipe);

        assertEquals(nouvelleEquipe, agent.getIdEquipe());
    }

    @Test
    void setStatut_doitModifierLaValeur() {
        AgentJpaEntity agent = new AgentJpaEntity(UUID.randomUUID(), UUID.randomUUID(), StatutAgent.ACTIF);

        agent.setStatut(StatutAgent.INACTIF);

        assertEquals(StatutAgent.INACTIF, agent.getStatut());
    }

    @Test
    void constructeurProtege_doitCreerInstanceVide() throws Exception {
        Object instance = AgentJpaEntity.class.getDeclaredConstructor().newInstance();
        assertNotNull(instance);
    }
}
