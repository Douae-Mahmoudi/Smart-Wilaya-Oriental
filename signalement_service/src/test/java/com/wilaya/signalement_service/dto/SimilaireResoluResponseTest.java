package com.wilaya.signalement_service.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SimilaireResoluResponseTest {

    @Test
    void devrait_exposer_existe_true_et_le_numero_de_suivi() {
        SimilaireResoluResponse reponse = new SimilaireResoluResponse(true, "SIG-20260806-DWES8N");

        assertThat(reponse.existe()).isTrue();
        assertThat(reponse.numeroSuivi()).isEqualTo("SIG-20260806-DWES8N");
    }

    @Test
    void devrait_exposer_existe_false_avec_numero_de_suivi_null() {
        SimilaireResoluResponse reponse = new SimilaireResoluResponse(false, null);

        assertThat(reponse.existe()).isFalse();
        assertThat(reponse.numeroSuivi()).isNull();
    }

    @Test
    void deux_instances_avec_les_memes_valeurs_devraient_etre_egales() {
        SimilaireResoluResponse reponse1 = new SimilaireResoluResponse(true, "SIG-1234");
        SimilaireResoluResponse reponse2 = new SimilaireResoluResponse(true, "SIG-1234");

        assertThat(reponse1).isEqualTo(reponse2);
        assertThat(reponse1.hashCode()).isEqualTo(reponse2.hashCode());
    }

    @Test
    void deux_instances_avec_des_valeurs_differentes_ne_devraient_pas_etre_egales() {
        SimilaireResoluResponse reponse1 = new SimilaireResoluResponse(true, "SIG-1234");
        SimilaireResoluResponse reponse2 = new SimilaireResoluResponse(true, "SIG-5678");

        assertThat(reponse1).isNotEqualTo(reponse2);
    }

    @Test
    void toString_devrait_contenir_existe_et_numeroSuivi() {
        SimilaireResoluResponse reponse = new SimilaireResoluResponse(true, "SIG-1234");

        assertThat(reponse.toString())
                .contains("true")
                .contains("SIG-1234");
    }
}






































