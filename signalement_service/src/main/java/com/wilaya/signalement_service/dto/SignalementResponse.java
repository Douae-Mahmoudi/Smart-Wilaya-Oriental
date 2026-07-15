package com.wilaya.signalement_service.dto;

import com.wilaya.signalement_service.model.Signalement;
import com.wilaya.signalement_service.model.NiveauGravite;
import com.wilaya.signalement_service.model.StatutSignalement;
import com.wilaya.signalement_service.model.TypeIntervention;

import java.time.LocalDateTime;
import java.util.UUID;


public record SignalementResponse(
        UUID id,
        String numeroSuivi,
        String cinDeclarant,
        TypeIntervention type,
        String description,
        String photoUrl,
        String zone,
        NiveauGravite gravite,
        StatutSignalement statut,
        LocalDateTime dateCreation
) {
    public static SignalementResponse depuis(Signalement signalement) {
        return new SignalementResponse(
                signalement.getId(),
                signalement.getNumeroSuivi(),
                signalement.getCinDeclarant(),
                signalement.getType(),
                signalement.getDescription(),
                signalement.getPhotoUrl(),
                signalement.getZone(),
                signalement.getGravite(),
                signalement.getStatut(),
                signalement.getDateCreation()
        );
    }
}
