package com.wilaya.affectation_service.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SignalementInfoTest {

    @Test
    void devrait_creer_un_signalement_info_avec_toutes_ses_valeurs() {
        UUID id = UUID.randomUUID();

        SignalementInfo info = new SignalementInfo(id, "EAU", "HAUTE", "Zone Nord");

        assertThat(info.id()).isEqualTo(id);
        assertThat(info.categorie()).isEqualTo("EAU");
        assertThat(info.gravite()).isEqualTo("HAUTE");
        assertThat(info.zone()).isEqualTo("Zone Nord");
    }

    @Test
    void deux_instances_avec_les_memes_valeurs_devraient_etre_egales() {
        UUID id = UUID.randomUUID();

        SignalementInfo info1 = new SignalementInfo(id, "EAU", "HAUTE", "Zone Nord");
        SignalementInfo info2 = new SignalementInfo(id, "EAU", "HAUTE", "Zone Nord");

        assertThat(info1).isEqualTo(info2);
        assertThat(info1.hashCode()).isEqualTo(info2.hashCode());
    }

    @Test
    void deux_instances_avec_des_ids_differents_ne_devraient_pas_etre_egales() {
        SignalementInfo info1 = new SignalementInfo(UUID.randomUUID(), "EAU", "HAUTE", "Zone Nord");
        SignalementInfo info2 = new SignalementInfo(UUID.randomUUID(), "EAU", "HAUTE", "Zone Nord");

        assertThat(info1).isNotEqualTo(info2);
    }

    @Test
    void deux_instances_avec_des_categories_differentes_ne_devraient_pas_etre_egales() {
        UUID id = UUID.randomUUID();

        SignalementInfo info1 = new SignalementInfo(id, "EAU", "HAUTE", "Zone Nord");
        SignalementInfo info2 = new SignalementInfo(id, "VOIRIE", "HAUTE", "Zone Nord");

        assertThat(info1).isNotEqualTo(info2);
    }

    @Test
    void devrait_accepter_des_champs_null() {
        SignalementInfo info = new SignalementInfo(null, null, null, null);

        assertThat(info.id()).isNull();
        assertThat(info.categorie()).isNull();
        assertThat(info.gravite()).isNull();
        assertThat(info.zone()).isNull();
    }

    @Test
    void toString_devrait_contenir_les_valeurs() {
        UUID id = UUID.randomUUID();
        SignalementInfo info = new SignalementInfo(id, "EAU", "HAUTE", "Zone Nord");

        assertThat(info.toString())
                .contains(id.toString())
                .contains("EAU")
                .contains("HAUTE")
                .contains("Zone Nord");
    }
}