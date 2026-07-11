package com.wilaya.ressource_service.dto;

import com.wilaya.ressource_service.model.CategorieIntervention;
import com.wilaya.ressource_service.model.Equipe;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EquipeDisponibleResponseTest {

    @Test
    void devrait_construire_une_reponse_depuis_une_equipe_avec_materiel_disponible() {
        Equipe equipe = new Equipe("Equipe Eau Nord", List.of(CategorieIntervention.EAU), "Zone Nord");

        EquipeDisponibleResponse response = EquipeDisponibleResponse.depuis(equipe, true);

        assertThat(response.nom()).isEqualTo("Equipe Eau Nord");
        assertThat(response.competences()).containsExactly(CategorieIntervention.EAU);
        assertThat(response.zoneCouverture()).isEqualTo("Zone Nord");
        assertThat(response.materielDisponible()).isTrue();
    }

    @Test
    void devrait_construire_une_reponse_depuis_une_equipe_sans_materiel_disponible() {
        Equipe equipe = new Equipe("Equipe Voirie Sud", List.of(CategorieIntervention.VOIRIE), "Zone Sud");

        EquipeDisponibleResponse response = EquipeDisponibleResponse.depuis(equipe, false);

        assertThat(response.materielDisponible()).isFalse();
    }

    @Test
    void devrait_conserver_lid_de_lequipe() {
        Equipe equipe = new Equipe("Equipe Test", List.of(CategorieIntervention.PROPRETE), "Zone Test");

        EquipeDisponibleResponse response = EquipeDisponibleResponse.depuis(equipe, true);

        assertThat(response.id()).isEqualTo(equipe.getId());
    }
}