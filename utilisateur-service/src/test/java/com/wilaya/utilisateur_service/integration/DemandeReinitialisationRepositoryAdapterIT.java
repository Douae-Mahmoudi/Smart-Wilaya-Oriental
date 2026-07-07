package com.wilaya.utilisateur_service.integration;

import com.wilaya.utilisateur_service.domain.model.DemandeReinitialisation;
import com.wilaya.utilisateur_service.infrastructure.persistence.DemandeReinitialisationRepositoryAdapter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = NONE)
@Import(DemandeReinitialisationRepositoryAdapter.class)
class DemandeReinitialisationRepositoryAdapterIT {

    @Autowired
    private DemandeReinitialisationRepositoryAdapter adapter;

    @Test
    void devrait_sauvegarder_et_retrouver_une_demande_en_attente() {
        UUID idUtilisateur = UUID.randomUUID();
        DemandeReinitialisation demande = DemandeReinitialisation.creer(idUtilisateur, "123456");

        adapter.save(demande);

        Optional<DemandeReinitialisation> resultat = adapter.findEnAttenteParUtilisateur(idUtilisateur, "123456");

        assertThat(resultat).isPresent();
    }

    @Test
    void ne_devrait_pas_trouver_une_demande_avec_mauvais_code() {
        UUID idUtilisateur = UUID.randomUUID();
        adapter.save(DemandeReinitialisation.creer(idUtilisateur, "123456"));

        Optional<DemandeReinitialisation> resultat = adapter.findEnAttenteParUtilisateur(idUtilisateur, "000000");

        assertThat(resultat).isEmpty();
    }

    @Test
    void ne_devrait_pas_trouver_une_demande_pour_un_autre_utilisateur() {
        UUID idUtilisateur = UUID.randomUUID();
        UUID autreUtilisateur = UUID.randomUUID();
        adapter.save(DemandeReinitialisation.creer(idUtilisateur, "123456"));

        Optional<DemandeReinitialisation> resultat = adapter.findEnAttenteParUtilisateur(autreUtilisateur, "123456");

        assertThat(resultat).isEmpty();
    }

    @Test
    void devrait_compter_les_demandes_recentes_pour_un_utilisateur() {
        UUID idUtilisateur = UUID.randomUUID();
        adapter.save(DemandeReinitialisation.creer(idUtilisateur, "111111"));
        adapter.save(DemandeReinitialisation.creer(idUtilisateur, "222222"));

        long compte = adapter.compterDemandesRecentes(idUtilisateur, Duration.ofMinutes(10));

        assertThat(compte).isEqualTo(2);
    }

    @Test
    void devrait_retourner_zero_si_aucune_demande_recente() {
        UUID idUtilisateur = UUID.randomUUID();

        long compte = adapter.compterDemandesRecentes(idUtilisateur, Duration.ofMinutes(10));

        assertThat(compte).isZero();
    }

    @Test
    void ne_devrait_pas_compter_les_demandes_dun_autre_utilisateur() {
        UUID idUtilisateur = UUID.randomUUID();
        UUID autreUtilisateur = UUID.randomUUID();
        adapter.save(DemandeReinitialisation.creer(idUtilisateur, "123456"));

        long compte = adapter.compterDemandesRecentes(autreUtilisateur, Duration.ofMinutes(10));

        assertThat(compte).isZero();
    }
}





























