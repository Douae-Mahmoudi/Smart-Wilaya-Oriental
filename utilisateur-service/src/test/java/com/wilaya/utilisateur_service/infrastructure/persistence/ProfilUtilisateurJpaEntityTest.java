package com.wilaya.utilisateur_service.infrastructure.persistence;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProfilUtilisateurJpaEntityTest {

    @Test
    void constructeur_doitInitialiserTousLesChamps() {
        UUID idKeycloak = UUID.randomUUID();
        String nom = "Benali";
        String prenom = "Karim";
        String telephone = "0555123456";
        String email = "karim.benali@example.com";
        boolean notificationsActivees = true;

        ProfilUtilisateurJpaEntity profil = new ProfilUtilisateurJpaEntity(
                idKeycloak, nom, prenom, telephone, email, notificationsActivees
        );

        assertEquals(idKeycloak, profil.getIdKeycloak());
        assertEquals(nom, profil.getNom());
        assertEquals(prenom, profil.getPrenom());
        assertEquals(telephone, profil.getTelephone());
        assertEquals(email, profil.getEmail());
        assertTrue(profil.isNotificationsActivees());
    }

    @Test
    void constructeur_doitAccepterNotificationsDesactivees() {
        ProfilUtilisateurJpaEntity profil = new ProfilUtilisateurJpaEntity(
                UUID.randomUUID(), "Meziane", "Sara", "0555987654",
                "sara.meziane@example.com", false
        );

        assertFalse(profil.isNotificationsActivees());
    }

    @Test
    void constructeurProtege_doitCreerInstanceVide() throws Exception {
        Object instance = ProfilUtilisateurJpaEntity.class.getDeclaredConstructor().newInstance();
        assertNotNull(instance);
    }
}
