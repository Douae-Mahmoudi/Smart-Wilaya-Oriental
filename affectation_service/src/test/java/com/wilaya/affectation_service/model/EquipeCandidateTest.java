package com.wilaya.affectation_service.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EquipeCandidateTest {

    @Test
    void devrait_creer_une_equipe_candidate_avec_toutes_ses_valeurs() {
        UUID id = UUID.randomUUID();

        EquipeCandidate candidate = new EquipeCandidate(id, 5.2, 3, true);

        assertThat(candidate.id()).isEqualTo(id);
        assertThat(candidate.distance()).isEqualTo(5.2);
        assertThat(candidate.chargeActuelle()).isEqualTo(3);
        assertThat(candidate.competenceExacte()).isTrue();
    }

    @Test
    void deux_instances_avec_les_memes_valeurs_devraient_etre_egales() {
        UUID id = UUID.randomUUID();

        EquipeCandidate candidate1 = new EquipeCandidate(id, 5.2, 3, true);
        EquipeCandidate candidate2 = new EquipeCandidate(id, 5.2, 3, true);

        assertThat(candidate1).isEqualTo(candidate2);
        assertThat(candidate1.hashCode()).isEqualTo(candidate2.hashCode());
    }

    @Test
    void deux_instances_avec_des_ids_differents_ne_devraient_pas_etre_egales() {
        EquipeCandidate candidate1 = new EquipeCandidate(UUID.randomUUID(), 5.2, 3, true);
        EquipeCandidate candidate2 = new EquipeCandidate(UUID.randomUUID(), 5.2, 3, true);

        assertThat(candidate1).isNotEqualTo(candidate2);
    }

    @Test
    void devrait_accepter_competence_exacte_a_false() {
        EquipeCandidate candidate = new EquipeCandidate(UUID.randomUUID(), 10.0, 0, false);

        assertThat(candidate.competenceExacte()).isFalse();
    }

    @Test
    void devrait_accepter_une_charge_actuelle_a_zero() {
        EquipeCandidate candidate = new EquipeCandidate(UUID.randomUUID(), 1.0, 0, true);

        assertThat(candidate.chargeActuelle()).isZero();
    }

    @Test
    void toString_devrait_contenir_les_valeurs() {
        UUID id = UUID.randomUUID();
        EquipeCandidate candidate = new EquipeCandidate(id, 5.2, 3, true);

        assertThat(candidate.toString())
                .contains(id.toString())
                .contains("5.2")
                .contains("3")
                .contains("true");
    }
}