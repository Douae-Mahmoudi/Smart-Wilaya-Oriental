package com.wilaya.ressource_service.integration;

import com.wilaya.ressource_service.model.CategorieIntervention;
import com.wilaya.ressource_service.model.Equipe;
import com.wilaya.ressource_service.model.StatutEquipe;
import com.wilaya.ressource_service.repository.EquipeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EquipeRepositoryIT {

    @Autowired
    private EquipeRepository equipeRepository;

    @Test
    void devrait_trouver_une_equipe_disponible_par_competence_et_zone() {
        Equipe equipe = new Equipe("Equipe Eau Nord", List.of(CategorieIntervention.EAU), "Zone Nord");
        equipeRepository.save(equipe);

        List<Equipe> resultat = equipeRepository.findDisponiblesParCompetenceEtZone(
                CategorieIntervention.EAU, "Zone Nord", StatutEquipe.DISPONIBLE);

        assertThat(resultat).hasSize(1);
        assertThat(resultat.get(0).getNom()).isEqualTo("Equipe Eau Nord");
    }

    @Test
    void ne_devrait_pas_trouver_une_equipe_dune_autre_zone() {
        Equipe equipe = new Equipe("Equipe Eau Nord", List.of(CategorieIntervention.EAU), "Zone Nord");
        equipeRepository.save(equipe);

        List<Equipe> resultat = equipeRepository.findDisponiblesParCompetenceEtZone(
                CategorieIntervention.EAU, "Zone Sud", StatutEquipe.DISPONIBLE);

        assertThat(resultat).isEmpty();
    }

    @Test
    void ne_devrait_pas_trouver_une_equipe_sans_la_competence() {
        Equipe equipe = new Equipe("Equipe Voirie", List.of(CategorieIntervention.VOIRIE), "Zone Nord");
        equipeRepository.save(equipe);

        List<Equipe> resultat = equipeRepository.findDisponiblesParCompetenceEtZone(
                CategorieIntervention.EAU, "Zone Nord", StatutEquipe.DISPONIBLE);

        assertThat(resultat).isEmpty();
    }

    @Test
    void ne_devrait_pas_trouver_une_equipe_hors_service() {
        Equipe equipe = new Equipe("Equipe Eau Nord", List.of(CategorieIntervention.EAU), "Zone Nord");
        equipe.changerStatut(StatutEquipe.HORS_SERVICE);
        equipeRepository.save(equipe);

        List<Equipe> resultat = equipeRepository.findDisponiblesParCompetenceEtZone(
                CategorieIntervention.EAU, "Zone Nord", StatutEquipe.DISPONIBLE);

        assertThat(resultat).isEmpty();
    }

    @Test
    void devrait_trouver_plusieurs_equipes_correspondantes() {
        equipeRepository.save(new Equipe("Equipe A", List.of(CategorieIntervention.EAU), "Zone Nord"));
        equipeRepository.save(new Equipe("Equipe B", List.of(CategorieIntervention.EAU), "Zone Nord"));

        List<Equipe> resultat = equipeRepository.findDisponiblesParCompetenceEtZone(
                CategorieIntervention.EAU, "Zone Nord", StatutEquipe.DISPONIBLE);

        assertThat(resultat).hasSize(2);
    }

    @Test
    void devrait_persister_la_version_pour_le_verrouillage_optimiste() {
        Equipe equipe = new Equipe("Equipe Test", List.of(CategorieIntervention.EAU), "Zone Test");
        Equipe sauvegardee = equipeRepository.save(equipe);

        assertThat(sauvegardee.getVersion()).isNotNull();
    }
}