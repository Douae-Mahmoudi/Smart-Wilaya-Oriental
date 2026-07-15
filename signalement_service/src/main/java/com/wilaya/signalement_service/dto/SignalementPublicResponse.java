package com.wilaya.signalement_service.dto;

import com.wilaya.signalement_service.model.Signalement;
import com.wilaya.signalement_service.model.NiveauGravite;
import com.wilaya.signalement_service.model.StatutSignalement;
import com.wilaya.signalement_service.model.TypeIntervention;

import java.time.LocalDateTime;


public record SignalementPublicResponse(
        String numeroSuivi,
        TypeIntervention type,
        String description,
        String zone,
        NiveauGravite gravite,
        StatutSignalement statut,
        LocalDateTime dateCreation,
        String cinMasque
) {
    public static SignalementPublicResponse depuis(Signalement signalement) {
        return new SignalementPublicResponse(
                signalement.getNumeroSuivi(),
                signalement.getType(),
                signalement.getDescription(),
                signalement.getZone(),
                signalement.getGravite(),
                signalement.getStatut(),
                signalement.getDateCreation(),
                signalement.masquerCin()
        );
    }
}
