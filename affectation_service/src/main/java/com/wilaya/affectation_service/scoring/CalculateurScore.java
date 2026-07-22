package com.wilaya.affectation_service.scoring;

import com.wilaya.affectation_service.model.EquipeCandidate;
import com.wilaya.affectation_service.model.SignalementInfo;
import org.springframework.stereotype.Component;

@Component
public class CalculateurScore {

    private static final double POIDS_DISTANCE = 0.4;
    private static final double POIDS_CHARGE = 0.3;
    private static final double POIDS_COMPETENCE = 0.2;
    private static final double POIDS_GRAVITE = 0.1;

    private static final double DISTANCE_MAX_KM = 50.0;
    private static final int CHARGE_MAX = 10;

    public Double calculer(EquipeCandidate equipe, SignalementInfo signalement) {
        if (equipe == null || signalement == null) {
            throw new IllegalArgumentException("Equipe et signalement ne peuvent pas être nuls");
        }

        double scoreDistance = calculerScoreDistance(equipe.distance());
        double scoreCharge = calculerScoreCharge(equipe.chargeActuelle());
        double scoreCompetence = Boolean.TRUE.equals(equipe.competenceExacte()) ? 1.0 : 0.0;
        double scoreGravite = calculerScoreGravite(signalement.gravite());

        double scoreTotal =
                (scoreDistance * POIDS_DISTANCE) +
                        (scoreCharge * POIDS_CHARGE) +
                        (scoreCompetence * POIDS_COMPETENCE) +
                        (scoreGravite * POIDS_GRAVITE);

        return Math.round(scoreTotal * 100.0) / 100.0;
    }

    private double calculerScoreDistance(Double distance) {
        if (distance == null || distance <= 0) {
            return 1.0;
        }
        double score = 1.0 - (distance / DISTANCE_MAX_KM);
        return Math.max(0.0, Math.min(1.0, score));
    }

    private double calculerScoreCharge(Integer chargeActuelle) {
        if (chargeActuelle == null || chargeActuelle <= 0) {
            return 1.0;
        }
        double score = 1.0 - ((double) chargeActuelle / CHARGE_MAX);
        return Math.max(0.0, Math.min(1.0, score));
    }

    private double calculerScoreGravite(String gravite) {
        if (gravite == null) {
            return 0.5;
        }
        return switch (gravite.toUpperCase()) {
            case "CRITIQUE" -> 1.0;
            case "ELEVEE" -> 0.75;
            case "MOYENNE" -> 0.5;
            case "FAIBLE" -> 0.25;
            default -> 0.5;
        };
    }
}