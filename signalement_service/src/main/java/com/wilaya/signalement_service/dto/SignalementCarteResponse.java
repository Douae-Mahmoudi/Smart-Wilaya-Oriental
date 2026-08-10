package com.wilaya.signalement_service.dto;

import com.wilaya.signalement_service.model.Signalement;
import com.wilaya.signalement_service.model.NiveauGravite;
import com.wilaya.signalement_service.model.StatutSignalement;
import com.wilaya.signalement_service.model.TypeIntervention;

import java.util.UUID;

public record SignalementCarteResponse(
        UUID id,
        String numeroSuivi,
        Double latitude,
        Double longitude,
        TypeIntervention type,
        NiveauGravite gravite,
        StatutSignalement statut
) {
    public static SignalementCarteResponse depuis(Signalement signalement) {
        return new SignalementCarteResponse(
                signalement.getId(),
                signalement.getNumeroSuivi(),
                signalement.getLatitude(),
                signalement.getLongitude(),
                signalement.getType(),
                signalement.getGravite(),
                signalement.getStatut()
        );
    }
}