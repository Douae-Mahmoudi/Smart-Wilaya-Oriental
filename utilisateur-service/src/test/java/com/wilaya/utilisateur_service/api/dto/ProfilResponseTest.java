package com.wilaya.utilisateur_service.api.dto;

import com.wilaya.utilisateur_service.domain.model.ProfilUtilisateur;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProfilResponseTest {

    @Test
    void depuisMappeTousLesChampsDuProfil() {
        ProfilUtilisateur profil = new ProfilUtilisateur(
                UUID.randomUUID(), "Benali", "Karim", "0600000000", "karim@example.com"
        );

        ProfilResponse response = ProfilResponse.depuis(profil);

        assertThat(response.nom()).isEqualTo("Benali");
        assertThat(response.prenom()).isEqualTo("Karim");
        assertThat(response.email()).isEqualTo("karim@example.com");
        assertThat(response.telephone()).isEqualTo("0600000000");
        assertThat(response.notificationsActivees()).isTrue();
    }

    @Test
    void depuisRefleteLesModificationsDuProfil() {
        ProfilUtilisateur profil = new ProfilUtilisateur(
                UUID.randomUUID(), "Benali", "Karim", "0600000000", "karim@example.com"
        );
        profil.modifierProfil("Alaoui", "Yassine", "0611111111");
        profil.desactiverNotifications();

        ProfilResponse response = ProfilResponse.depuis(profil);

        assertThat(response.nom()).isEqualTo("Alaoui");
        assertThat(response.prenom()).isEqualTo("Yassine");
        assertThat(response.telephone()).isEqualTo("0611111111");
        assertThat(response.notificationsActivees()).isFalse();
    }

    @Test
    void depuisNIncluPasLIdKeycloakDansLaReponse() {

        ProfilUtilisateur profil = new ProfilUtilisateur(
                UUID.randomUUID(), "Benali", "Karim", "0600000000", "karim@example.com"
        );

        ProfilResponse response = ProfilResponse.depuis(profil);

        assertThat(response.getClass().getRecordComponents())
                .extracting(java.lang.reflect.RecordComponent::getName)
                .isNotEmpty()
                .doesNotContain("idKeycloak");
    }
}