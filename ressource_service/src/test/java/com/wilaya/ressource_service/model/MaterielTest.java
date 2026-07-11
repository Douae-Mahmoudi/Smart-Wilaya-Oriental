package com.wilaya.ressource_service.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class MaterielTest {

    @Test
    void constructeurInitialiseLeStatutADisponible() {
        UUID idEquipe = UUID.randomUUID();

        Materiel materiel = new Materiel("Camion citerne", idEquipe);

        assertThat(materiel.getStatut()).isEqualTo(StatutMateriel.DISPONIBLE);
    }

    @Test
    void constructeurInitialiseCorrectementLesChamps() {
        UUID idEquipe = UUID.randomUUID();

        Materiel materiel = new Materiel("Camion citerne", idEquipe);

        assertThat(materiel.getType()).isEqualTo("Camion citerne");
        assertThat(materiel.getIdEquipeAssociee()).isEqualTo(idEquipe);
    }

    @Test
    void estDisponibleRenvoieVraiQuandStatutDisponible() {
        Materiel materiel = new Materiel("Camion citerne", UUID.randomUUID());

        assertThat(materiel.estDisponible()).isTrue();
    }

    @Test
    void estDisponibleRenvoieFauxQuandStatutEnUsage() {
        Materiel materiel = new Materiel("Camion citerne", UUID.randomUUID());
        materiel.changerStatut(StatutMateriel.EN_USAGE);

        assertThat(materiel.estDisponible()).isFalse();
    }

    @Test
    void estDisponibleRenvoieFauxQuandStatutEnMaintenance() {
        Materiel materiel = new Materiel("Camion citerne", UUID.randomUUID());
        materiel.changerStatut(StatutMateriel.EN_MAINTENANCE);

        assertThat(materiel.estDisponible()).isFalse();
    }

    @Test
    void changerStatutModifieLeStatut() {
        Materiel materiel = new Materiel("Camion citerne", UUID.randomUUID());

        materiel.changerStatut(StatutMateriel.EN_MAINTENANCE);

        assertThat(materiel.getStatut()).isEqualTo(StatutMateriel.EN_MAINTENANCE);
    }

    @Test
    void changerStatutPeutRevenirADisponible() {
        Materiel materiel = new Materiel("Camion citerne", UUID.randomUUID());
        materiel.changerStatut(StatutMateriel.EN_USAGE);

        materiel.changerStatut(StatutMateriel.DISPONIBLE);

        assertThat(materiel.getStatut()).isEqualTo(StatutMateriel.DISPONIBLE);
        assertThat(materiel.estDisponible()).isTrue();
    }
}
