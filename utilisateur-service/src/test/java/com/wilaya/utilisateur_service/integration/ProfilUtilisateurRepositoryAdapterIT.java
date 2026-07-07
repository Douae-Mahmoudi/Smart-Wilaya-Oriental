package com.wilaya.utilisateur_service.integration;

import com.wilaya.utilisateur_service.domain.model.ProfilUtilisateur;
import com.wilaya.utilisateur_service.infrastructure.persistence.ProfilUtilisateurRepositoryAdapter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = NONE)
@Import(ProfilUtilisateurRepositoryAdapter.class)
class ProfilUtilisateurRepositoryAdapterIT {

    @Autowired
    private ProfilUtilisateurRepositoryAdapter adapter;

    @Test
    void devrait_sauvegarder_et_retrouver_un_profil_par_id() {
        UUID id = UUID.randomUUID();
        ProfilUtilisateur profil = new ProfilUtilisateur(id, "Benali", "Ahmed", "0600000000", "ahmed@test.com");

        adapter.save(profil);

        Optional<ProfilUtilisateur> resultat = adapter.findByIdKeycloak(id);

        assertThat(resultat).isPresent();
        assertThat(resultat.get().getEmail()).isEqualTo("ahmed@test.com");
        assertThat(resultat.get().getNom()).isEqualTo("Benali");
        assertThat(resultat.get().getPrenom()).isEqualTo("Ahmed");
    }

    @Test
    void devrait_retrouver_un_profil_par_email() {
        UUID id = UUID.randomUUID();
        ProfilUtilisateur profil = new ProfilUtilisateur(id, "Alaoui", "Sara", "0600000001", "sara@test.com");
        adapter.save(profil);

        Optional<ProfilUtilisateur> resultat = adapter.findByEmail("sara@test.com");

        assertThat(resultat).isPresent();
        assertThat(resultat.get().getIdKeycloak()).isEqualTo(id);
    }

    @Test
    void devrait_retourner_vide_si_email_inconnu() {
        Optional<ProfilUtilisateur> resultat = adapter.findByEmail("inconnu@test.com");

        assertThat(resultat).isEmpty();
    }

    @Test
    void devrait_retourner_vide_si_id_inconnu() {
        Optional<ProfilUtilisateur> resultat = adapter.findByIdKeycloak(UUID.randomUUID());

        assertThat(resultat).isEmpty();
    }

    @Test
    void devrait_conserver_les_notifications_activees_par_defaut() {
        UUID id = UUID.randomUUID();
        ProfilUtilisateur profil = new ProfilUtilisateur(id, "Idrissi", "Youssef", "0600000002", "youssef@test.com");
        adapter.save(profil);

        Optional<ProfilUtilisateur> resultat = adapter.findByIdKeycloak(id);

        assertThat(resultat).isPresent();
        assertThat(resultat.get().isNotificationsActivees()).isTrue();
    }
}





























