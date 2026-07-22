package com.wilaya.affectation_service.scoring;

import com.wilaya.affectation_service.model.EquipeCandidate;
import com.wilaya.affectation_service.model.SignalementInfo;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CalculateurScoreTest {

    private final CalculateurScore calculateurScore = new CalculateurScore();

    @Test
    void calculer_devraitLeverException_quandEquipeEstNulle() {
        SignalementInfo signalement = new SignalementInfo(UUID.randomUUID(), "Voirie", "Elevee", "Zone Nord");
        assertThrows(IllegalArgumentException.class, () -> calculateurScore.calculer(null, signalement));
    }

    @Test
    void calculer_devraitLeverException_quandSignalementEstNul() {
        EquipeCandidate equipe = new EquipeCandidate(UUID.randomUUID(), 10.0, 2, true);
        assertThrows(IllegalArgumentException.class, () -> calculateurScore.calculer(equipe, null));
    }

    @Test
    void calculer_devraitRetournerScoreMaximal_quandEquipeIdeale() {
        EquipeCandidate equipe = new EquipeCandidate(UUID.randomUUID(), 0.0, 0, true);
        SignalementInfo signalement = new SignalementInfo(UUID.randomUUID(), "Voirie", "CRITIQUE", "Zone Nord");

        Double score = calculateurScore.calculer(equipe, signalement);

        assertEquals(1.0, score);
    }

    @Test
    void calculer_devraitRetournerScoreMinimal_quandEquipeLoinChargeeEtSansCompetence() {
        EquipeCandidate equipe = new EquipeCandidate(UUID.randomUUID(), 50.0, 10, false);
        SignalementInfo signalement = new SignalementInfo(UUID.randomUUID(), "Voirie", "FAIBLE", "Zone Nord");

        Double score = calculateurScore.calculer(equipe, signalement);

        assertEquals(0.03, score);
    }

    @Test
    void calculer_devraitPlafonnerScoreDistanceAZero_quandDistanceDepasseMax() {
        EquipeCandidate equipe = new EquipeCandidate(UUID.randomUUID(), 100.0, 0, true);
        SignalementInfo signalement = new SignalementInfo(UUID.randomUUID(), "Voirie", "CRITIQUE", "Zone Nord");

        Double score = calculateurScore.calculer(equipe, signalement);

        assertEquals(0.6, score);
    }

    @Test
    void calculer_devraitTraiterDistanceNulle_commeScoreMaximal() {
        EquipeCandidate equipe = new EquipeCandidate(UUID.randomUUID(), null, 0, true);
        SignalementInfo signalement = new SignalementInfo(UUID.randomUUID(), "Voirie", "CRITIQUE", "Zone Nord");

        Double score = calculateurScore.calculer(equipe, signalement);

        assertEquals(1.0, score);
    }

    @Test
    void calculer_devraitTraiterChargeNulle_commeScoreMaximal() {
        EquipeCandidate equipe = new EquipeCandidate(UUID.randomUUID(), 0.0, null, true);
        SignalementInfo signalement = new SignalementInfo(UUID.randomUUID(), "Voirie", "CRITIQUE", "Zone Nord");

        Double score = calculateurScore.calculer(equipe, signalement);

        assertEquals(1.0, score);
    }

    @Test
    void calculer_devraitPlafonnerChargeAZero_quandChargeDepasseMax() {
        EquipeCandidate equipe = new EquipeCandidate(UUID.randomUUID(), 0.0, 20, true);
        SignalementInfo signalement = new SignalementInfo(UUID.randomUUID(), "Voirie", "CRITIQUE", "Zone Nord");

        Double score = calculateurScore.calculer(equipe, signalement);

        assertEquals(0.7, score); // Remplacé 0.9 par 0.7
    }

    @Test
    void calculer_devraitAttribuerZero_quandCompetenceNonExacte() {
        EquipeCandidate equipe = new EquipeCandidate(UUID.randomUUID(), 0.0, 0, false);
        SignalementInfo signalement = new SignalementInfo(UUID.randomUUID(), "Voirie", "CRITIQUE", "Zone Nord");

        Double score = calculateurScore.calculer(equipe, signalement);

        assertEquals(0.8, score);
    }

    @Test
    void calculer_devraitAttribuerZero_quandCompetenceEstNulle() {
        EquipeCandidate equipe = new EquipeCandidate(UUID.randomUUID(), 0.0, 0, null);
        SignalementInfo signalement = new SignalementInfo(UUID.randomUUID(), "Voirie", "CRITIQUE", "Zone Nord");

        Double score = calculateurScore.calculer(equipe, signalement);

        assertEquals(0.8, score);
    }

    @Test
    void calculer_devraitAttribuerScoreDemi_quandGraviteEstNulle() {
        EquipeCandidate equipe = new EquipeCandidate(UUID.randomUUID(), 0.0, 0, true);
        SignalementInfo signalement = new SignalementInfo(UUID.randomUUID(), "Voirie", null, "Zone Nord");

        Double score = calculateurScore.calculer(equipe, signalement);

        assertEquals(0.95, score);
    }

    @Test
    void calculer_devraitAttribuerScoreDemi_quandGraviteInconnue() {
        EquipeCandidate equipe = new EquipeCandidate(UUID.randomUUID(), 0.0, 0, true);
        SignalementInfo signalement = new SignalementInfo(UUID.randomUUID(), "Voirie", "INCONNUE", "Zone Nord");

        Double score = calculateurScore.calculer(equipe, signalement);

        assertEquals(0.95, score);
    }

    @Test
    void calculer_devraitEtreInsensibleALaCasse_pourGravite() {
        EquipeCandidate equipe = new EquipeCandidate(UUID.randomUUID(), 0.0, 0, true);
        SignalementInfo signalement = new SignalementInfo(UUID.randomUUID(), "Voirie", "critique", "Zone Nord");

        Double score = calculateurScore.calculer(equipe, signalement);

        assertEquals(1.0, score);
    }

    @Test
    void calculer_devraitCalculerScoreIntermediaire_pourGraviteMoyenne() {
        EquipeCandidate equipe = new EquipeCandidate(UUID.randomUUID(), 0.0, 0, true);
        SignalementInfo signalement = new SignalementInfo(UUID.randomUUID(), "Voirie", "MOYENNE", "Zone Nord");

        Double score = calculateurScore.calculer(equipe, signalement);

        assertEquals(0.95, score);
    }
}



