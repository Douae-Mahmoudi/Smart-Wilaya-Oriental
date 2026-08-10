package com.wilaya.signalement_service.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class HaversineUtilTest {

    @Test
    void distance_devrait_etre_zero_pour_le_meme_point() {
        double distance = HaversineUtil.distanceInMeters(34.68, -1.90, 34.68, -1.90);

        assertThat(distance).isZero();
    }

    @Test
    void distance_devrait_etre_zero_a_l_origine() {
        double distance = HaversineUtil.distanceInMeters(0.0, 0.0, 0.0, 0.0);

        assertThat(distance).isZero();
    }

    @Test
    void distance_devrait_etre_symetrique() {
        double distanceAB = HaversineUtil.distanceInMeters(34.68, -1.90, 35.20, -2.50);
        double distanceBA = HaversineUtil.distanceInMeters(35.20, -2.50, 34.68, -1.90);

        assertThat(distanceAB).isCloseTo(distanceBA, within(0.001));
    }

    @Test
    void distance_devrait_correspondre_a_environ_111_km_par_degre_de_latitude() {
        double distance = HaversineUtil.distanceInMeters(34.0, -1.90, 35.0, -1.90);

        assertThat(distance).isCloseTo(111_195.0, within(50.0));
    }

    @Test
    void distance_devrait_correspondre_a_environ_222_km_pour_2_degres_de_longitude_a_l_equateur() {
        double distance = HaversineUtil.distanceInMeters(0.0, -1.0, 0.0, 1.0);

        assertThat(distance).isCloseTo(222_390.0, within(200.0));
    }

    @Test
    void distance_paris_londres_devrait_correspondre_a_la_distance_connue() {
        double distanceMetres = HaversineUtil.distanceInMeters(
                48.8566, 2.3522,
                51.5074, -0.1278
        );

        assertThat(distanceMetres).isCloseTo(343_500.0, within(3_000.0));
    }

    @Test
    void distance_devrait_detecter_une_localisation_quasi_exacte_sous_20_metres() {
        double distance = HaversineUtil.distanceInMeters(34.68, -1.90, 34.68018, -1.90);

        assertThat(distance).isCloseTo(20.0, within(2.0));
    }

    @Test
    void distance_devrait_augmenter_avec_l_ecart_de_coordonnees() {
        double distanceProche = HaversineUtil.distanceInMeters(34.68, -1.90, 34.6801, -1.90);
        double distanceLoin = HaversineUtil.distanceInMeters(34.68, -1.90, 35.68, -1.90);

        assertThat(distanceLoin).isGreaterThan(distanceProche);
    }
}
