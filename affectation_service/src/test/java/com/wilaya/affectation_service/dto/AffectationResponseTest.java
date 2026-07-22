package com.wilaya.affectation_service.dto;

import com.wilaya.affectation_service.model.StatutTentative;
import com.wilaya.affectation_service.model.TentativeAffectation;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AffectationResponseTest {

    private final UUID idSignalement = UUID.randomUUID();
    private final UUID idEquipe = UUID.randomUUID();

    @Test
    void depuis_devrait_mapper_tous_les_champs_dune_tentative_en_attente() {
        TentativeAffectation tentative = new TentativeAffectation(
                idSignalement, idEquipe, 0.75, 15, "EAU", "HAUTE", "Zone Nord");

        AffectationResponse response = AffectationResponse.depuis(tentative);

        assertThat(response.idSignalement()).isEqualTo(idSignalement);
        assertThat(response.idEquipeProposee()).isEqualTo(idEquipe);
        assertThat(response.score()).isEqualTo(0.75);
        assertThat(response.statut()).isEqualTo(StatutTentative.EN_ATTENTE);
        assertThat(response.dateProposition()).isEqualTo(tentative.getDateProposition());
        assertThat(response.dateExpiration()).isEqualTo(tentative.getDateExpiration());
        assertThat(response.dateReponse()).isNull();
    }

    @Test
    void depuis_devrait_refleter_le_statut_accepte_et_la_date_reponse() {
        TentativeAffectation tentative = new TentativeAffectation(
                idSignalement, idEquipe, 0.5, 15, "EAU", "HAUTE", "Zone Nord");
        tentative.accepter();

        AffectationResponse response = AffectationResponse.depuis(tentative);

        assertThat(response.statut()).isEqualTo(StatutTentative.ACCEPTEE);
        assertThat(response.dateReponse()).isNotNull();
    }

    @Test
    void depuis_devrait_refleter_le_statut_refuse() {
        TentativeAffectation tentative = new TentativeAffectation(
                idSignalement, idEquipe, 0.5, 15, "EAU", "HAUTE", "Zone Nord");
        tentative.refuser();

        AffectationResponse response = AffectationResponse.depuis(tentative);

        assertThat(response.statut()).isEqualTo(StatutTentative.REFUSEE);
        assertThat(response.dateReponse()).isNotNull();
    }

    @Test
    void depuis_devrait_avoir_id_null_pour_une_tentative_non_persistee() {
        TentativeAffectation tentative = new TentativeAffectation(
                idSignalement, idEquipe, 0.5, 15, "EAU", "HAUTE", "Zone Nord");

        AffectationResponse response = AffectationResponse.depuis(tentative);

        assertThat(response.id()).isNull();
    }
}