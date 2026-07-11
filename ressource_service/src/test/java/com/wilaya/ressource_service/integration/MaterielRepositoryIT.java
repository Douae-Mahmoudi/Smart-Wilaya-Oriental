package com.wilaya.ressource_service.integration;


import com.wilaya.ressource_service.model.Materiel;
import com.wilaya.ressource_service.repository.MaterielRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MaterielRepositoryIT {

    @Autowired
    private MaterielRepository materielRepository;

    @Test
    void devrait_trouver_le_materiel_dune_equipe() {
        UUID idEquipe = UUID.randomUUID();
        materielRepository.save(new Materiel("Camion citerne", idEquipe));
        materielRepository.save(new Materiel("Pompe", idEquipe));

        List<Materiel> resultat = materielRepository.findByIdEquipeAssociee(idEquipe);

        assertThat(resultat).hasSize(2);
    }

    @Test
    void ne_devrait_pas_trouver_le_materiel_dune_autre_equipe() {
        UUID idEquipe = UUID.randomUUID();
        UUID autreEquipe = UUID.randomUUID();
        materielRepository.save(new Materiel("Camion citerne", idEquipe));

        List<Materiel> resultat = materielRepository.findByIdEquipeAssociee(autreEquipe);

        assertThat(resultat).isEmpty();
    }

    @Test
    void devrait_retourner_liste_vide_si_aucun_materiel() {
        List<Materiel> resultat = materielRepository.findByIdEquipeAssociee(UUID.randomUUID());

        assertThat(resultat).isEmpty();
    }

    @Test
    void devrait_sauvegarder_le_statut_par_defaut_disponible() {
        Materiel materiel = new Materiel("Camion", UUID.randomUUID());
        Materiel sauvegarde = materielRepository.save(materiel);

        assertThat(sauvegarde.estDisponible()).isTrue();
    }
}