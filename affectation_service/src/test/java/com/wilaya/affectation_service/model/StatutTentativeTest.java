package com.wilaya.affectation_service.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StatutTentativeTest {

    @Test
    void devrait_contenir_exactement_quatre_valeurs() {
        StatutTentative[] valeurs = StatutTentative.values();

        assertThat(valeurs).hasSize(4);
    }

    @Test
    void devrait_contenir_les_quatre_statuts_attendus() {
        assertThat(StatutTentative.values())
                .containsExactlyInAnyOrder(
                        StatutTentative.EN_ATTENTE,
                        StatutTentative.ACCEPTEE,
                        StatutTentative.REFUSEE,
                        StatutTentative.EXPIREE
                );
    }

    @Test
    void valueOf_devrait_retrouver_chaque_statut_par_son_nom() {
        assertThat(StatutTentative.valueOf("EN_ATTENTE")).isEqualTo(StatutTentative.EN_ATTENTE);
        assertThat(StatutTentative.valueOf("ACCEPTEE")).isEqualTo(StatutTentative.ACCEPTEE);
        assertThat(StatutTentative.valueOf("REFUSEE")).isEqualTo(StatutTentative.REFUSEE);
        assertThat(StatutTentative.valueOf("EXPIREE")).isEqualTo(StatutTentative.EXPIREE);
    }

    @Test
    void valueOf_devrait_lever_exception_pour_un_statut_inconnu() {
        org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> StatutTentative.valueOf("INEXISTANT")
        );
    }
}