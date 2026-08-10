package com.wilaya.affectation_service.integration;

import com.wilaya.affectation_service.model.StatutTentative;
import com.wilaya.affectation_service.model.TentativeAffectation;
import com.wilaya.affectation_service.repository.TentativeAffectationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TentativeAffectationRepositoryIT {

    @Autowired
    private TentativeAffectationRepository repository;

    private TentativeAffectation creerTentative(UUID idSignalement, UUID idEquipe) {
        return new TentativeAffectation(idSignalement, idEquipe, 0.75, 15, "EAU", "HAUTE", "Zone Nord",
                "Fuite d'eau importante", "Rue des Fleurs");
    }

    @Test
    void devrait_trouver_les_tentatives_par_signalement() {
        UUID idSignalement = UUID.randomUUID();
        repository.save(creerTentative(idSignalement, UUID.randomUUID()));
        repository.save(creerTentative(UUID.randomUUID(), UUID.randomUUID()));

        List<TentativeAffectation> resultat = repository.findByIdSignalement(idSignalement);

        assertThat(resultat).hasSize(1);
        assertThat(resultat.get(0).getIdSignalement()).isEqualTo(idSignalement);
    }

    @Test
    void devrait_retourner_liste_vide_si_aucune_tentative_pour_ce_signalement() {
        List<TentativeAffectation> resultat = repository.findByIdSignalement(UUID.randomUUID());

        assertThat(resultat).isEmpty();
    }

    @Test
    void devrait_trouver_les_tentatives_par_statut() {
        TentativeAffectation enAttente = creerTentative(UUID.randomUUID(), UUID.randomUUID());
        TentativeAffectation acceptee = creerTentative(UUID.randomUUID(), UUID.randomUUID());
        acceptee.accepter(UUID.randomUUID());

        repository.save(enAttente);
        repository.save(acceptee);

        List<TentativeAffectation> resultat = repository.findByStatut(StatutTentative.EN_ATTENTE);

        assertThat(resultat).hasSize(1);
        assertThat(resultat.get(0).getStatut()).isEqualTo(StatutTentative.EN_ATTENTE);
    }

    @Test
    void devrait_trouver_plusieurs_tentatives_avec_le_meme_statut() {
        repository.save(creerTentative(UUID.randomUUID(), UUID.randomUUID()));
        repository.save(creerTentative(UUID.randomUUID(), UUID.randomUUID()));

        List<TentativeAffectation> resultat = repository.findByStatut(StatutTentative.EN_ATTENTE);

        assertThat(resultat).hasSize(2);
    }

    @Test
    void devrait_trouver_une_tentative_par_signalement_et_statut() {
        UUID idSignalement = UUID.randomUUID();
        TentativeAffectation tentative = creerTentative(idSignalement, UUID.randomUUID());
        repository.save(tentative);

        List<TentativeAffectation> resultat = repository.findByIdSignalementAndStatut(
                idSignalement, StatutTentative.EN_ATTENTE);

        assertThat(resultat).hasSize(1);
    }

    @Test
    void ne_devrait_pas_trouver_une_tentative_si_le_statut_ne_correspond_pas() {
        UUID idSignalement = UUID.randomUUID();
        repository.save(creerTentative(idSignalement, UUID.randomUUID()));

        List<TentativeAffectation> resultat = repository.findByIdSignalementAndStatut(
                idSignalement, StatutTentative.ACCEPTEE);

        assertThat(resultat).isEmpty();
    }

    @Test
    void ne_devrait_pas_trouver_une_tentative_dun_autre_signalement_meme_avec_le_bon_statut() {
        UUID idSignalement = UUID.randomUUID();
        UUID autreSignalement = UUID.randomUUID();
        repository.save(creerTentative(idSignalement, UUID.randomUUID()));

        List<TentativeAffectation> resultat = repository.findByIdSignalementAndStatut(
                autreSignalement, StatutTentative.EN_ATTENTE);

        assertThat(resultat).isEmpty();
    }

    @Test
    void devrait_persister_et_retrouver_par_id_genere_automatiquement() {
        TentativeAffectation tentative = creerTentative(UUID.randomUUID(), UUID.randomUUID());

        TentativeAffectation sauvegardee = repository.save(tentative);

        assertThat(sauvegardee.getId()).isNotNull();
        assertThat(repository.findById(sauvegardee.getId())).isPresent();
    }
}






















































































