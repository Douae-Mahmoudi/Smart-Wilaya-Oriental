package com.wilaya.signalement_service.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class RapportIAResponseTest {

    @Test
    void devrait_exposer_les_valeurs_passees_au_constructeur() {
        LocalDateTime date = LocalDateTime.of(2026, 8, 8, 10, 30);
        RapportIAResponse reponse = new RapportIAResponse("Contenu du rapport", date);

        assertThat(reponse.contenu()).isEqualTo("Contenu du rapport");
        assertThat(reponse.dateGeneration()).isEqualTo(date);
    }

    @Test
    void deux_instances_avec_les_memes_valeurs_devraient_etre_egales() {
        LocalDateTime date = LocalDateTime.of(2026, 8, 8, 10, 30);
        RapportIAResponse reponse1 = new RapportIAResponse("Contenu identique", date);
        RapportIAResponse reponse2 = new RapportIAResponse("Contenu identique", date);

        assertThat(reponse1).isEqualTo(reponse2);
        assertThat(reponse1.hashCode()).isEqualTo(reponse2.hashCode());
    }

    @Test
    void deux_instances_avec_des_valeurs_differentes_ne_devraient_pas_etre_egales() {
        LocalDateTime date = LocalDateTime.of(2026, 8, 8, 10, 30);
        RapportIAResponse reponse1 = new RapportIAResponse("Contenu A", date);
        RapportIAResponse reponse2 = new RapportIAResponse("Contenu B", date);

        assertThat(reponse1).isNotEqualTo(reponse2);
    }

    @Test
    void toString_devrait_contenir_le_contenu_et_la_date() {
        LocalDateTime date = LocalDateTime.of(2026, 8, 8, 10, 30);
        RapportIAResponse reponse = new RapportIAResponse("Rapport test", date);

        assertThat(reponse.toString())
                .contains("Rapport test")
                .contains("2026-08-08T10:30");
    }

    @Test
    void devrait_accepter_un_contenu_null_car_aucune_validation_declaree() {
        RapportIAResponse reponse = new RapportIAResponse(null, null);

        assertThat(reponse.contenu()).isNull();
        assertThat(reponse.dateGeneration()).isNull();
    }
}
