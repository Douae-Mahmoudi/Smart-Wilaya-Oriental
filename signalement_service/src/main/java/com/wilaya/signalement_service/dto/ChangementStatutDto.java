package com.wilaya.signalement_service.dto;

import com.wilaya.signalement_service.model.ChangementStatut;
import com.wilaya.signalement_service.model.StatutSignalement;
import java.time.LocalDateTime;

public record ChangementStatutDto(
        StatutSignalement nouveauStatut,
        String message,
        LocalDateTime dateChangement
) {
    public static ChangementStatutDto depuis(ChangementStatut changement) {
        return new ChangementStatutDto(
                changement.getNouveauStatut(),
                changement.getMessage(),
                changement.getDateChangement()
        );
    }
}