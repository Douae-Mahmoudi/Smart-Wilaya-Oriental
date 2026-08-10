package com.wilaya.signalement_service.dto;

import com.wilaya.signalement_service.model.Signalement;
import com.wilaya.signalement_service.model.NiveauGravite;
import com.wilaya.signalement_service.model.StatutSignalement;
import com.wilaya.signalement_service.model.TypeIntervention;

import java.time.LocalDateTime;
import java.util.List;

public record SignalementPublicResponse(
        String numeroSuivi,
        TypeIntervention type,
        String description,
        String zone,
        Double latitude,
        Double longitude,
        NiveauGravite gravite,
        StatutSignalement statut,
        LocalDateTime dateCreation,
        String cinMasque,
        String dernierMessage,
        List<ChangementStatutDto> historiqueStatuts
) {
    public static SignalementPublicResponse depuis(Signalement signalement) {
        List<ChangementStatutDto> historique = signalement.getHistoriqueStatuts()
                .stream()
                .map(ChangementStatutDto::depuis)
                .toList();

        return new SignalementPublicResponse(
                signalement.getNumeroSuivi(),
                signalement.getType(),
                signalement.getDescription(),
                signalement.getZone(),
                signalement.getLatitude(),
                signalement.getLongitude(),
                signalement.getGravite(),
                signalement.getStatut(),
                signalement.getDateCreation(),
                signalement.masquerCin(),
                signalement.getDernierMessage(),
                historique
        );
    }
}
