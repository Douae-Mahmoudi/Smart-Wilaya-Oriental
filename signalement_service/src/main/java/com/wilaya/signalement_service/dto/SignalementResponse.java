package com.wilaya.signalement_service.dto;

import com.wilaya.signalement_service.model.Signalement;
import com.wilaya.signalement_service.model.NiveauGravite;
import com.wilaya.signalement_service.model.StatutSignalement;
import com.wilaya.signalement_service.model.TypeIntervention;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record SignalementResponse(
        UUID id,
        String numeroSuivi,
        String cinDeclarant,
        TypeIntervention type,
        String description,
        String photoUrl,
        String zone,
        String adresse,
        Double latitude,
        Double longitude,
        NiveauGravite gravite,
        StatutSignalement statut,
        LocalDateTime dateCreation,
        String dernierMessage,
        List<ChangementStatutDto> historiqueStatuts
) {
    public static SignalementResponse depuis(Signalement signalement) {
        List<ChangementStatutDto> historique = signalement.getHistoriqueStatuts()
                .stream()
                .map(ChangementStatutDto::depuis)
                .collect(Collectors.toList());

        return new SignalementResponse(
                signalement.getId(),
                signalement.getNumeroSuivi(),
                signalement.getCinDeclarant(),
                signalement.getType(),
                signalement.getDescription(),
                signalement.getPhotoUrl(),
                signalement.getZone(),
                signalement.getAdresse(),
                signalement.getLatitude(),
                signalement.getLongitude(),
                signalement.getGravite(),
                signalement.getStatut(),
                signalement.getDateCreation(),
                signalement.getDernierMessage(),
                historique
        );
    }
}