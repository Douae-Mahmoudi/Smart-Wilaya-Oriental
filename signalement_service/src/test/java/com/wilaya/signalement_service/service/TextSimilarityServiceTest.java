package com.wilaya.signalement_service.service;

import com.wilaya.signalement_service.util.TfIdfVectorizer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TextSimilarityServiceTest {

    @Mock
    private TfIdfVectorizer vectorizer;

    private TextSimilarityService service;

    private void construire() {
        service = new TextSimilarityService(vectorizer);

        ReflectionTestUtils.setField(service, "similarityThreshold", 0.6);
    }

    @Test
    void computeSimilarity_devrait_retourner_0_si_desc1_est_null() {
        construire();

        double resultat = service.computeSimilarity(null, "Une description valide");

        assertThat(resultat).isZero();
    }

    @Test
    void computeSimilarity_devrait_retourner_0_si_desc2_est_null() {
        construire();

        double resultat = service.computeSimilarity("Une description valide", null);

        assertThat(resultat).isZero();
    }

    @Test
    void computeSimilarity_devrait_retourner_0_si_apres_nettoyage_une_description_est_vide() {
        construire();

        double resultat = service.computeSimilarity("le la de et", "Une vraie description");

        assertThat(resultat).isZero();
    }

    @Test
    void computeSimilarity_devrait_calculer_le_cosinus_correctement() {
        construire();

        Map<String, Double> vecteur1 = Map.of("eau", 2.0, "coupure", 1.0);
        Map<String, Double> vecteur2 = Map.of("eau", 2.0, "alimentation", 1.5);

        when(vectorizer.getTfIdfVector(anyString())).thenReturn(vecteur1, vecteur2);

        double resultat = service.computeSimilarity("Fuite d'eau importante", "Coupure alimentation quartier");

        assertThat(resultat).isCloseTo(0.7158, within(0.001));
    }

    @Test
    void computeSimilarity_devrait_retourner_1_pour_deux_vecteurs_identiques() {
        construire();

        Map<String, Double> vecteur = Map.of("eau", 2.0, "coupure", 1.0);
        when(vectorizer.getTfIdfVector(anyString())).thenReturn(vecteur, vecteur);

        double resultat = service.computeSimilarity("Texte A", "Texte B");

        assertThat(resultat).isCloseTo(1.0, within(0.0001));
    }

    @Test
    void computeSimilarity_devrait_retourner_0_si_aucun_terme_en_commun() {
        construire();

        Map<String, Double> vecteur1 = Map.of("eau", 2.0);
        Map<String, Double> vecteur2 = Map.of("route", 1.5);
        when(vectorizer.getTfIdfVector(anyString())).thenReturn(vecteur1, vecteur2);

        double resultat = service.computeSimilarity("Texte A", "Texte B");

        assertThat(resultat).isZero();
    }

    @Test
    void computeSimilarity_devrait_retourner_0_si_un_vecteur_est_vide() {
        construire();

        when(vectorizer.getTfIdfVector(anyString())).thenReturn(Map.of(), Map.of("eau", 1.0));

        double resultat = service.computeSimilarity("Texte A", "Texte B");

        assertThat(resultat).isZero();
    }

    @Test
    void areSimilar_devrait_retourner_true_si_similarite_atteint_le_seuil() {
        construire();

        Map<String, Double> vecteur = Map.of("eau", 2.0, "coupure", 1.0);
        when(vectorizer.getTfIdfVector(anyString())).thenReturn(vecteur, vecteur);

        assertThat(service.areSimilar("Texte A", "Texte B")).isTrue();
    }

    @Test
    void areSimilar_devrait_retourner_false_si_similarite_sous_le_seuil() {
        construire();

        Map<String, Double> vecteur1 = Map.of("eau", 2.0);
        Map<String, Double> vecteur2 = Map.of("route", 1.5);
        when(vectorizer.getTfIdfVector(anyString())).thenReturn(vecteur1, vecteur2);

        assertThat(service.areSimilar("Texte A", "Texte B")).isFalse();
    }

    @Test
    void areSimilar_devrait_respecter_un_seuil_personnalise() {
        construire();
        ReflectionTestUtils.setField(service, "similarityThreshold", 0.9);

        Map<String, Double> vecteur1 = Map.of("eau", 2.0, "coupure", 1.0);
        Map<String, Double> vecteur2 = Map.of("eau", 2.0, "alimentation", 1.5);
        when(vectorizer.getTfIdfVector(anyString())).thenReturn(vecteur1, vecteur2);

        assertThat(service.areSimilar("Texte A", "Texte B")).isFalse();
    }

    @Test
    void getSimilarityThreshold_devrait_retourner_la_valeur_configuree() {
        construire();
        ReflectionTestUtils.setField(service, "similarityThreshold", 0.75);

        assertThat(service.getSimilarityThreshold()).isEqualTo(0.75);
    }
}








































