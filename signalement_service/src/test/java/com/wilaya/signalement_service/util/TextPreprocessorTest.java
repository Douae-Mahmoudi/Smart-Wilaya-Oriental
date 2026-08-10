package com.wilaya.signalement_service.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TextPreprocessorTest {

    @Test
    void stemAvecTexteNullRenvoieChaineVide() {
        assertThat(TextPreprocessor.stem(null)).isEmpty();
    }

    @Test
    void stemAvecTexteVideRenvoieChaineVide() {
        assertThat(TextPreprocessor.stem("")).isEmpty();
        assertThat(TextPreprocessor.stem("   ")).isEmpty();
    }

    @Test
    void stemAvecUniquementDePonctuationRenvoieChaineVide() {
        assertThat(TextPreprocessor.stem("!!! ??? ...")).isEmpty();
    }

    @Test
    void stemFiltreLesStopwords() {
        String avecStopwords = TextPreprocessor.stem("une fuite dans la canalisation");
        String sansStopwords = TextPreprocessor.stem("fuite canalisation");

        assertThat(avecStopwords).isEqualTo(sansStopwords);
    }

    @Test
    void stemEstInsensibleALaCasse() {
        assertThat(TextPreprocessor.stem("ROUTE")).isEqualTo(TextPreprocessor.stem("route"));
        assertThat(TextPreprocessor.stem("Électricité")).isEqualTo(TextPreprocessor.stem("electricite"));
    }

    @Test
    void synonymesDeCoupureConvergentVersLeMemeTerme() {
        String reference = TextPreprocessor.stem("coupure");

        assertThat(TextPreprocessor.stem("interruption")).isEqualTo(reference);
        assertThat(TextPreprocessor.stem("panne")).isEqualTo(reference);
        assertThat(TextPreprocessor.stem("suspension")).isEqualTo(reference);
        assertThat(TextPreprocessor.stem("arret")).isEqualTo(reference);
        assertThat(TextPreprocessor.stem("indisponible")).isEqualTo(reference);
    }

    @Test
    void synonymesDElectriciteConvergentVersLeMemeTermeMemeAvecAccents() {
        String reference = TextPreprocessor.stem("electricite");

        assertThat(TextPreprocessor.stem("électrique")).isEqualTo(reference);
        assertThat(TextPreprocessor.stem("electrique")).isEqualTo(reference);
        assertThat(TextPreprocessor.stem("courant")).isEqualTo(reference);
        assertThat(TextPreprocessor.stem("énergie")).isEqualTo(reference);
    }

    @Test
    void synonymesDeRouteConvergentVersLeMemeTerme() {
        String reference = TextPreprocessor.stem("route");

        assertThat(TextPreprocessor.stem("chaussee")).isEqualTo(reference);
        assertThat(TextPreprocessor.stem("voirie")).isEqualTo(reference);
        assertThat(TextPreprocessor.stem("voie")).isEqualTo(reference);
    }

    @Test
    void synonymesDeDechetsConvergentVersLeMemeTerme() {
        String reference = TextPreprocessor.stem("dechets");

        assertThat(TextPreprocessor.stem("ordures")).isEqualTo(reference);
        assertThat(TextPreprocessor.stem("detritus")).isEqualTo(reference);
        assertThat(TextPreprocessor.stem("immondices")).isEqualTo(reference);
    }

    @Test
    void phraseNidDePouleConvergeVersLeMemeTermeQueTrouEtCavite() {
        String reference = TextPreprocessor.stem("trou");

        assertThat(TextPreprocessor.stem("cavite")).isEqualTo(reference);
        assertThat(TextPreprocessor.stem("nid-de-poule")).isEqualTo(reference);
        assertThat(TextPreprocessor.stem("nid de poule")).isEqualTo(reference);
    }

    @Test
    void phraseBorneFontaineConvergeVersRobinet() {
        String reference = TextPreprocessor.stem("robinet");

        assertThat(TextPreprocessor.stem("borne-fontaine")).isEqualTo(reference);
        assertThat(TextPreprocessor.stem("borne fontaine")).isEqualTo(reference);
    }

    @Test
    void phraseEspaceVertConvergeVersLeMemeTermeQueVerdure() {
        String reference = TextPreprocessor.stem("verdure");

        assertThat(TextPreprocessor.stem("espace vert")).isEqualTo(reference);
        assertThat(TextPreprocessor.stem("espaces verts")).isEqualTo(reference);
    }

    @Test
    void motHorsDictionnaireResteTraiteSansErreur() {
        // "poteau" n'appartient à aucun groupe de synonymes : il doit simplement
        // être racinisé normalement, sans lever d'exception ni disparaître.
        assertThat(TextPreprocessor.stem("poteau")).isNotBlank();
    }

    @Test
    void deuxSujetsDifferentsNePartagentPasLeursTermesCanoniques() {
        String eau = TextPreprocessor.stem("fuite d'eau");
        String route = TextPreprocessor.stem("trou dans la route");

        String canonEau = TextPreprocessor.stem("eau");
        String canonRoute = TextPreprocessor.stem("route");

        assertThat(eau).doesNotContain(canonRoute);
        assertThat(route).doesNotContain(canonEau);
    }

    @Test
    void exempleReelPanneElectriqueVsInterruptionFournitureElectricite() {
        String phrase1 = TextPreprocessor.stem("Une panne électrique prive les habitants de courant.");
        String phrase2 = TextPreprocessor.stem(
                "Le secteur est confronté à une interruption de la fourniture d'électricité.");

        String canonCoupure = TextPreprocessor.stem("coupure");
        String canonElectricite = TextPreprocessor.stem("electricite");

        assertThat(phrase1).contains(canonCoupure).contains(canonElectricite);
        assertThat(phrase2).contains(canonCoupure).contains(canonElectricite);
    }
}


























































































