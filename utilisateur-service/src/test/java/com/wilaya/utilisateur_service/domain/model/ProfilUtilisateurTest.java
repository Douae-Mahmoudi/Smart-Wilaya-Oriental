package com.wilaya.utilisateur_service.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProfilUtilisateurTest {

    private static final UUID ID_KEYCLOAK = UUID.randomUUID();
    private static final String ROLE = "AGENT";

    @Test
    void creerUnProfilAvecUnEmailValideFonctionne() {
        ProfilUtilisateur profil = new ProfilUtilisateur(
                ID_KEYCLOAK, "Benali", "Karim", "0600000000", "karim.benali@example.com", ROLE
        );

        assertThat(profil.getIdKeycloak()).isEqualTo(ID_KEYCLOAK);
        assertThat(profil.getNom()).isEqualTo("Benali");
        assertThat(profil.getPrenom()).isEqualTo("Karim");
        assertThat(profil.getTelephone()).isEqualTo("0600000000");
        assertThat(profil.getEmail()).isEqualTo("karim.benali@example.com");
        assertThat(profil.getRole()).isEqualTo(ROLE);
    }

    @Test
    void lesNotificationsSontActiveesParDefaut() {
        ProfilUtilisateur profil = new ProfilUtilisateur(
                ID_KEYCLOAK, "Benali", "Karim", "0600000000", "karim.benali@example.com", ROLE
        );

        assertThat(profil.isNotificationsActivees()).isTrue();
    }

    @Test
    void creerUnProfilAvecUnEmailNullLanceUneException() {
        assertThatThrownBy(() -> new ProfilUtilisateur(
                ID_KEYCLOAK, "Benali", "Karim", "0600000000", null, ROLE
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email invalide");
    }

    @Test
    void creerUnProfilAvecUnEmailSansArobaseLanceUneException() {
        assertThatThrownBy(() -> new ProfilUtilisateur(
                ID_KEYCLOAK, "Benali", "Karim", "0600000000", "email-invalide", ROLE
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email invalide");
    }

    @Test
    void modifierProfilMetAJourNomPrenomEtTelephone() {
        ProfilUtilisateur profil = new ProfilUtilisateur(
                ID_KEYCLOAK, "Benali", "Karim", "0600000000", "karim.benali@example.com", ROLE
        );

        profil.modifierProfil("Alaoui", "Yassine", "0611111111");

        assertThat(profil.getNom()).isEqualTo("Alaoui");
        assertThat(profil.getPrenom()).isEqualTo("Yassine");
        assertThat(profil.getTelephone()).isEqualTo("0611111111");
    }

    @Test
    void modifierProfilNeChangePasLEmailNiLIdKeycloak() {
        ProfilUtilisateur profil = new ProfilUtilisateur(
                ID_KEYCLOAK, "Benali", "Karim", "0600000000", "karim.benali@example.com", ROLE
        );

        profil.modifierProfil("Alaoui", "Yassine", "0611111111");

        assertThat(profil.getEmail()).isEqualTo("karim.benali@example.com");
        assertThat(profil.getIdKeycloak()).isEqualTo(ID_KEYCLOAK);
    }

    @Test
    void desactiverNotificationsPasseLeFlagAFaux() {
        ProfilUtilisateur profil = new ProfilUtilisateur(
                ID_KEYCLOAK, "Benali", "Karim", "0600000000", "karim.benali@example.com", ROLE
        );

        profil.desactiverNotifications();

        assertThat(profil.isNotificationsActivees()).isFalse();
    }

    @Test
    void activerNotificationsPasseLeFlagAVrai() {
        ProfilUtilisateur profil = new ProfilUtilisateur(
                ID_KEYCLOAK, "Benali", "Karim", "0600000000", "karim.benali@example.com", ROLE
        );
        profil.desactiverNotifications();

        profil.activerNotifications();

        assertThat(profil.isNotificationsActivees()).isTrue();
    }
}





































