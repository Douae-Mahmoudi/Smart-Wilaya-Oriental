package com.wilaya.utilisateur_service.infrastructure.persistence;

import com.wilaya.utilisateur_service.domain.model.StatutAgent;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AgentJpaEntityTest {

    @Test
    void constructeurInitialiseTousLesChamps() {
        UUID idProfil = UUID.randomUUID();
        UUID idEquipe = UUID.randomUUID();
        StatutAgent statut = StatutAgent.ACTIF;

        AgentJpaEntity entity = new AgentJpaEntity(idProfil, idEquipe, statut);

        assertThat(entity.getIdProfil()).isEqualTo(idProfil);
        assertThat(entity.getIdEquipe()).isEqualTo(idEquipe);
        assertThat(entity.getStatut()).isEqualTo(statut);
    }

    @Test
    void getIdProfilRenvoieLIdProfilCorrect() {
        UUID idProfil = UUID.randomUUID();

        AgentJpaEntity entity = new AgentJpaEntity(idProfil, UUID.randomUUID(), StatutAgent.ACTIF);

        assertThat(entity.getIdProfil()).isEqualTo(idProfil);
    }

    @Test
    void getIdEquipeRenvoieLIdEquipeCorrect() {
        UUID idEquipe = UUID.randomUUID();

        AgentJpaEntity entity = new AgentJpaEntity(UUID.randomUUID(), idEquipe, StatutAgent.ACTIF);

        assertThat(entity.getIdEquipe()).isEqualTo(idEquipe);
    }

    @Test
    void getStatutRenvoieLeStatutCorrect() {
        AgentJpaEntity entity = new AgentJpaEntity(UUID.randomUUID(), UUID.randomUUID(), StatutAgent.INACTIF);

        assertThat(entity.getStatut()).isEqualTo(StatutAgent.INACTIF);
    }

    @Test
    void constructeurAccepteIdEquipeNull() {
        UUID idProfil = UUID.randomUUID();

        AgentJpaEntity entity = new AgentJpaEntity(idProfil, null, StatutAgent.ACTIF);

        assertThat(entity.getIdProfil()).isEqualTo(idProfil);
        assertThat(entity.getIdEquipe()).isNull();
    }
}