package com.wilaya.affectation_service.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TentativeAffectationTest {

    private final UUID idSignalement = UUID.randomUUID();
    private final UUID idEquipe = UUID.randomUUID();
    private final UUID idAgent = UUID.randomUUID();

    private TentativeAffectation creerTentative(int dureeValiditeMinutes) {
        return new TentativeAffectation(
                idSignalement, idEquipe, 0.5, dureeValiditeMinutes, "EAU", "HAUTE", "Zone Nord",
                "Description test", "Adresse test");
    }

    @Test
    void devrait_creer_une_tentative_en_attente_par_defaut() {
        TentativeAffectation tentative = new TentativeAffectation(
                idSignalement, idEquipe, 0.75, 15, "EAU", "HAUTE", "Zone Nord",
                "Description test", "Adresse test");

        assertThat(tentative.getStatut()).isEqualTo(StatutTentative.EN_ATTENTE);
        assertThat(tentative.getIdSignalement()).isEqualTo(idSignalement);
        assertThat(tentative.getIdEquipeProposee()).isEqualTo(idEquipe);
        assertThat(tentative.getScore()).isEqualTo(0.75);
        assertThat(tentative.getCategorie()).isEqualTo("EAU");
        assertThat(tentative.getGravite()).isEqualTo("HAUTE");
        assertThat(tentative.getZone()).isEqualTo("Zone Nord");
        assertThat(tentative.getDateReponse()).isNull();
    }

    @Test
    void devrait_calculer_la_date_expiration_selon_la_duree_donnee() {
        TentativeAffectation tentative = creerTentative(15);

        LocalDateTime attendu = tentative.getDateProposition().plusMinutes(15);

        assertThat(tentative.getDateExpiration()).isEqualTo(attendu);
    }

    @Test
    void ne_devrait_pas_etre_expiree_juste_apres_creation() {
        TentativeAffectation tentative = creerTentative(15);

        assertThat(tentative.estExpiree()).isFalse();
    }

    @Test
    void devrait_etre_expiree_si_la_duree_de_validite_est_deja_depassee() {
        TentativeAffectation tentative = creerTentative(-1);

        assertThat(tentative.estExpiree()).isTrue();
    }


    @Test
    void accepter_devrait_passer_le_statut_a_acceptee_et_enregistrer_lagent() {
        TentativeAffectation tentative = creerTentative(15);

        tentative.accepter(idAgent);

        assertThat(tentative.getStatut()).isEqualTo(StatutTentative.ACCEPTEE);
        assertThat(tentative.getDateReponse()).isNotNull();
        assertThat(tentative.getIdAgentAccepteur()).isEqualTo(idAgent);
    }

    @Test
    void accepter_devrait_rejeter_une_tentative_deja_acceptee() {
        TentativeAffectation tentative = creerTentative(15);
        tentative.accepter(idAgent);

        assertThatThrownBy(() -> tentative.accepter(idAgent))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ACCEPTEE");
    }

    @Test
    void accepter_devrait_rejeter_une_tentative_deja_refusee() {
        TentativeAffectation tentative = creerTentative(15);
        tentative.refuser();

        assertThatThrownBy(() -> tentative.accepter(idAgent))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("REFUSEE");
    }

    @Test
    void accepter_devrait_lever_exception_et_marquer_expiree_si_delai_depasse() {
        TentativeAffectation tentative = creerTentative(-1);

        assertThatThrownBy(() -> tentative.accepter(idAgent))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("expiré");

        assertThat(tentative.getStatut()).isEqualTo(StatutTentative.EXPIREE);
    }


    @Test
    void refuser_devrait_passer_le_statut_a_refusee() {
        TentativeAffectation tentative = creerTentative(15);

        tentative.refuser();

        assertThat(tentative.getStatut()).isEqualTo(StatutTentative.REFUSEE);
        assertThat(tentative.getDateReponse()).isNotNull();
    }

    @Test
    void refuser_devrait_rejeter_une_tentative_deja_acceptee() {
        TentativeAffectation tentative = creerTentative(15);
        tentative.accepter(idAgent);

        assertThatThrownBy(tentative::refuser)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ACCEPTEE");
    }

    @Test
    void refuser_devrait_rejeter_une_tentative_deja_refusee() {
        TentativeAffectation tentative = creerTentative(15);
        tentative.refuser();

        assertThatThrownBy(tentative::refuser)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("REFUSEE");
    }

    @Test
    void refuser_ne_verifie_pas_lexpiration_contrairement_a_accepter() {
        TentativeAffectation tentative = creerTentative(-1);

        tentative.refuser();

        assertThat(tentative.getStatut()).isEqualTo(StatutTentative.REFUSEE);
    }


    @Test
    void marquerExpiree_devrait_passer_le_statut_a_expiree_si_en_attente() {
        TentativeAffectation tentative = creerTentative(15);

        tentative.marquerExpiree();

        assertThat(tentative.getStatut()).isEqualTo(StatutTentative.EXPIREE);
    }

    @Test
    void marquerExpiree_ne_devrait_rien_changer_si_deja_acceptee() {
        TentativeAffectation tentative = creerTentative(15);
        tentative.accepter(idAgent);

        tentative.marquerExpiree();

        assertThat(tentative.getStatut()).isEqualTo(StatutTentative.ACCEPTEE);
    }

    @Test
    void marquerExpiree_ne_devrait_rien_changer_si_deja_refusee() {
        TentativeAffectation tentative = creerTentative(15);
        tentative.refuser();

        tentative.marquerExpiree();

        assertThat(tentative.getStatut()).isEqualTo(StatutTentative.REFUSEE);
    }
}

























































































