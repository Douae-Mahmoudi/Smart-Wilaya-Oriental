package com.wilaya.signalement_service.model;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

class ChangementStatutTest {

    private static final ZoneId FUSEAU_MAROC = ZoneId.of("Africa/Casablanca");

    private Signalement signalementExemple() {
        return new Signalement("AB123456", TypeIntervention.EAU, "Fuite d'eau", "photo.jpg", "Zone Nord",
                NiveauGravite.MOYENNE, "Rue Test", 34.68, -1.90);
    }

    @Test
    void constructeur_devrait_initialiser_signalement_statut_et_message() {
        Signalement signalement = signalementExemple();

        ChangementStatut changement = new ChangementStatut(signalement, StatutSignalement.CLASSIFIE,
                "Classifié automatiquement après création.");

        assertThat(changement.getSignalement()).isEqualTo(signalement);
        assertThat(changement.getNouveauStatut()).isEqualTo(StatutSignalement.CLASSIFIE);
        assertThat(changement.getMessage()).isEqualTo("Classifié automatiquement après création.");
    }

    @Test
    void constructeur_devrait_fixer_la_date_de_changement_au_moment_present() {
        LocalDateTime avant = LocalDateTime.now(FUSEAU_MAROC);

        ChangementStatut changement = new ChangementStatut(signalementExemple(), StatutSignalement.RESOLU, "Résolu");

        LocalDateTime apres = LocalDateTime.now(FUSEAU_MAROC);

        assertThat(changement.getDateChangement()).isNotNull();
        assertThat(changement.getDateChangement()).isBetween(avant, apres.plusSeconds(1));
    }

    @Test
    void dateChangement_ne_devrait_pas_deriver_de_plus_dune_seconde_du_fuseau_maroc() {
        LocalDateTime attendu = LocalDateTime.now(FUSEAU_MAROC);

        ChangementStatut changement = new ChangementStatut(signalementExemple(), StatutSignalement.EN_INTERVENTION, "En cours");

        long ecartSecondes = Math.abs(Duration.between(attendu, changement.getDateChangement()).getSeconds());
        assertThat(ecartSecondes).isLessThanOrEqualTo(1);
    }

    @Test
    void id_devrait_etre_null_avant_persistance() {
        // @GeneratedValue -> l'id n'est attribué qu'à l'enregistrement en
        // base par JPA/Hibernate, jamais par le constructeur lui-même.
        ChangementStatut changement = new ChangementStatut(signalementExemple(), StatutSignalement.AFFECTE, "Affecté");

        assertThat(changement.getId()).isNull();
    }

    @Test
    void devrait_avoir_un_constructeur_protege_sans_argument_pour_jpa() throws NoSuchMethodException {
        Constructor<ChangementStatut> constructeurVide = ChangementStatut.class.getDeclaredConstructor();

        assertThat(Modifier.isProtected(constructeurVide.getModifiers())).isTrue();
    }

    @Test
    void devrait_accepter_un_message_null() {
        ChangementStatut changement = new ChangementStatut(signalementExemple(), StatutSignalement.CLOTURE, null);

        assertThat(changement.getMessage()).isNull();
    }
}








































