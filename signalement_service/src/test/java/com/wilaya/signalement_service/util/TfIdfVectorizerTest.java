package com.wilaya.signalement_service.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class TfIdfVectorizerTest {

    private TfIdfVectorizer vectorizer;

    @BeforeEach
    void setUp() {
        vectorizer = new TfIdfVectorizer();
    }

    private static double idfAttendu(int documentFrequency, int totalDocuments) {
        return Math.log((totalDocuments + 1.0) / (documentFrequency + 1.0)) + 1.0;
    }

    @Test
    void avantToutRebuildIndexTotalDocumentsEstZero() {
        assertThat(vectorizer.getTotalDocuments()).isZero();
    }

    @Test
    void avantToutRebuildIndexLesTermesInconnusUtilisentUneIdfParDefautDeUn() {
        Map<String, Double> vecteur = vectorizer.getTfIdfVector("chat");

        assertThat(vecteur.get("chat")).isCloseTo(1.0, within(1e-9));
    }

    @Test
    void rebuildIndexAvecListeVideReinitialiseLIndex() {
        vectorizer.rebuildIndex(List.of("chat chien", "chat oiseau"));
        assertThat(vectorizer.getTotalDocuments()).isEqualTo(2);

        vectorizer.rebuildIndex(List.of());

        assertThat(vectorizer.getTotalDocuments()).isZero();
        Map<String, Double> vecteur = vectorizer.getTfIdfVector("chat");
        assertThat(vecteur.get("chat")).isCloseTo(1.0, within(1e-9));
    }

    @Test
    void rebuildIndexCompteCorrectementLeNombreDeDocuments() {
        vectorizer.rebuildIndex(List.of("chat chien", "chat oiseau", "chien oiseau souris"));

        assertThat(vectorizer.getTotalDocuments()).isEqualTo(3);
    }

    @Test
    void rebuildIndexCalculeLidfSelonLaFrequenceDocumentaire() {
        vectorizer.rebuildIndex(List.of("chat chien", "chat oiseau", "chien oiseau souris"));

        double idfChat = vectorizer.getTfIdfVector("chat").get("chat");
        double idfSouris = vectorizer.getTfIdfVector("souris").get("souris");

        assertThat(idfChat).isCloseTo(idfAttendu(2, 3), within(1e-9));
        assertThat(idfSouris).isCloseTo(idfAttendu(1, 3), within(1e-9));
        assertThat(idfSouris).isGreaterThan(idfChat);
    }

    @Test
    void rebuildIndexCompteUnTermeRepeteDansUnMemeDocumentUneSeuleFoisPourLaDf() {
        vectorizer.rebuildIndex(List.of("chat chat chat", "chien seul"));

        double idfChat = vectorizer.getTfIdfVector("chat").get("chat");

        assertThat(idfChat).isCloseTo(idfAttendu(1, 2), within(1e-9));
    }

    @Test
    void getTfIdfVectorAppliqueUnLissageLogarithmiqueSurLaFrequenceDuTerme() {
        vectorizer.rebuildIndex(List.of("chat chien", "chat oiseau"));

        double idfChat = idfAttendu(2, 2);

        double valeurUneOccurrence = vectorizer.getTfIdfVector("chat").get("chat");
        double valeurTroisOccurrences = vectorizer.getTfIdfVector("chat chat chat").get("chat");

        double tfUneOccurrence = 1.0 + Math.log(1);
        double tfTroisOccurrences = 1.0 + Math.log(3);

        assertThat(valeurUneOccurrence).isCloseTo(tfUneOccurrence * idfChat, within(1e-9));
        assertThat(valeurTroisOccurrences).isCloseTo(tfTroisOccurrences * idfChat, within(1e-9));
        assertThat(valeurTroisOccurrences).isGreaterThan(valeurUneOccurrence);
    }

    @Test
    void getTfIdfVectorUtiliseUneIdfParDefautPourUnTermeAbsentDuCorpus() {
        vectorizer.rebuildIndex(List.of("chat chien", "chat oiseau"));

        Map<String, Double> vecteur = vectorizer.getTfIdfVector("girafe");

        assertThat(vecteur.get("girafe")).isCloseTo(1.0, within(1e-9));
    }

    @Test
    void getTfIdfVectorRenvoieUneEntreeParTermeDistinct() {
        vectorizer.rebuildIndex(List.of("chat chien", "chat oiseau"));

        Map<String, Double> vecteur = vectorizer.getTfIdfVector("chat chien chat");

        assertThat(vecteur).containsOnlyKeys("chat", "chien");
    }
}
