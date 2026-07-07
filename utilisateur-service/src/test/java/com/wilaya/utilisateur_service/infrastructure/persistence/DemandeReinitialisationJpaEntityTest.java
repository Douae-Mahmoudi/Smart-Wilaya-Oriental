package com.wilaya.utilisateur_service.infrastructure.persistence;

import com.wilaya.utilisateur_service.domain.model.StatutDemande;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DemandeReinitialisationJpaEntityTest {

    @Test
    void constructeur_doitInitialiserTousLesChamps() {
        UUID id = UUID.randomUUID();
        UUID idUtilisateur = UUID.randomUUID();
        String code = "123456";
        LocalDateTime dateCreation = LocalDateTime.now();
        LocalDateTime dateExpiration = dateCreation.plusMinutes(15);
        StatutDemande statut = StatutDemande.EN_ATTENTE;

        DemandeReinitialisationJpaEntity demande = new DemandeReinitialisationJpaEntity(
                id, idUtilisateur, code, dateCreation, dateExpiration, statut
        );

        assertEquals(id, demande.getId());
        assertEquals(idUtilisateur, demande.getIdUtilisateur());
        assertEquals(code, demande.getCode());
        assertEquals(dateCreation, demande.getDateCreation());
        assertEquals(dateExpiration, demande.getDateExpiration());
        assertEquals(statut, demande.getStatut());
    }

    @Test
    void constructeur_doitAccepterStatutUtilise() {
        DemandeReinitialisationJpaEntity demande = new DemandeReinitialisationJpaEntity(
                UUID.randomUUID(), UUID.randomUUID(), "654321",
                LocalDateTime.now(), LocalDateTime.now().plusMinutes(15),
                StatutDemande.UTILISE
        );

        assertEquals(StatutDemande.UTILISE, demande.getStatut());
    }

    @Test
    void constructeur_doitAccepterStatutExpire() {
        DemandeReinitialisationJpaEntity demande = new DemandeReinitialisationJpaEntity(
                UUID.randomUUID(), UUID.randomUUID(), "111111",
                LocalDateTime.now().minusMinutes(30), LocalDateTime.now().minusMinutes(15),
                StatutDemande.EXPIRE
        );

        assertEquals(StatutDemande.EXPIRE, demande.getStatut());
    }

    @Test
    void constructeurProtege_doitCreerInstanceVide() throws Exception {
        Object instance = DemandeReinitialisationJpaEntity.class.getDeclaredConstructor().newInstance();
        assertNotNull(instance);
    }
}













