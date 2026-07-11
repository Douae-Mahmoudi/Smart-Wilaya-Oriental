package com.wilaya.ressource_service.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EquipeTest {

    @Test
    void constructeurInitialiseLeStatutADisponible() {
        Equipe equipe = new Equipe("Equipe Eau Nord", List.of(CategorieIntervention.EAU), "Zone Nord");

        assertThat(equipe.getStatut()).isEqualTo(StatutEquipe.DISPONIBLE);
    }

    @Test
    void constructeurInitialiseCorrectementLesChamps() {
        Equipe equipe = new Equipe("Equipe Eau Nord", List.of(CategorieIntervention.EAU), "Zone Nord");

        assertThat(equipe.getNom()).isEqualTo("Equipe Eau Nord");
        assertThat(equipe.getCompetences()).containsExactly(CategorieIntervention.EAU);
        assertThat(equipe.getZoneCouverture()).isEqualTo("Zone Nord");
    }

    @Test
    void possedeCompetenceRenvoieVraiSiLaCompetenceEstPresente() {
        Equipe equipe = new Equipe("Equipe Eau Nord", List.of(CategorieIntervention.EAU, CategorieIntervention.VOIRIE), "Zone Nord");

        assertThat(equipe.possedeCompetence(CategorieIntervention.EAU)).isTrue();
        assertThat(equipe.possedeCompetence(CategorieIntervention.VOIRIE)).isTrue();
    }

    @Test
    void possedeCompetenceRenvoieFauxSiLaCompetenceEstAbsente() {
        Equipe equipe = new Equipe("Equipe Eau Nord", List.of(CategorieIntervention.EAU), "Zone Nord");

        assertThat(equipe.possedeCompetence(CategorieIntervention.ELECTRICITE)).isFalse();
    }

    @Test
    void estDisponiblePourRenvoieVraiSiDisponibleEtCompetente() {
        Equipe equipe = new Equipe("Equipe Eau Nord", List.of(CategorieIntervention.EAU), "Zone Nord");

        assertThat(equipe.estDisponiblePour(CategorieIntervention.EAU)).isTrue();
    }

    @Test
    void estDisponiblePourRenvoieFauxSiCompetenteMaisNonDisponible() {
        Equipe equipe = new Equipe("Equipe Eau Nord", List.of(CategorieIntervention.EAU), "Zone Nord");
        equipe.changerStatut(StatutEquipe.EN_INTERVENTION);

        assertThat(equipe.estDisponiblePour(CategorieIntervention.EAU)).isFalse();
    }

    @Test
    void estDisponiblePourRenvoieFauxSiDisponibleMaisNonCompetente() {
        Equipe equipe = new Equipe("Equipe Eau Nord", List.of(CategorieIntervention.EAU), "Zone Nord");

        assertThat(equipe.estDisponiblePour(CategorieIntervention.PROPRETE)).isFalse();
    }

    @Test
    void changerStatutModifieLeStatut() {
        Equipe equipe = new Equipe("Equipe Eau Nord", List.of(CategorieIntervention.EAU), "Zone Nord");

        equipe.changerStatut(StatutEquipe.HORS_SERVICE);

        assertThat(equipe.getStatut()).isEqualTo(StatutEquipe.HORS_SERVICE);
    }

    @Test
    void changerStatutVersDisponibleDepuisEnInterventionFonctionne() {
        Equipe equipe = new Equipe("Equipe Eau Nord", List.of(CategorieIntervention.EAU), "Zone Nord");
        equipe.changerStatut(StatutEquipe.EN_INTERVENTION);

        equipe.changerStatut(StatutEquipe.DISPONIBLE);

        assertThat(equipe.getStatut()).isEqualTo(StatutEquipe.DISPONIBLE);
    }

    @Test
    void changerStatutVersEnInterventionDepuisEnInterventionLanceUneException() {
        Equipe equipe = new Equipe("Equipe Eau Nord", List.of(CategorieIntervention.EAU), "Zone Nord");
        equipe.changerStatut(StatutEquipe.EN_INTERVENTION);

        assertThatThrownBy(() -> equipe.changerStatut(StatutEquipe.EN_INTERVENTION))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cette équipe est déjà affectée à une intervention");
    }

    @Test
    void changerStatutVersEnInterventionDepuisDisponibleFonctionne() {
        Equipe equipe = new Equipe("Equipe Eau Nord", List.of(CategorieIntervention.EAU), "Zone Nord");

        equipe.changerStatut(StatutEquipe.EN_INTERVENTION);

        assertThat(equipe.getStatut()).isEqualTo(StatutEquipe.EN_INTERVENTION);
    }
}







