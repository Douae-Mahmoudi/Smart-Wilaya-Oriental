package com.wilaya.signalement_service.dto;

import com.wilaya.signalement_service.model.Signalement;


public record CreerSignalementResponse(
        String numeroSuivi,
        String message
) {
    public static CreerSignalementResponse depuis(Signalement signalement) {
        return new CreerSignalementResponse(
                signalement.getNumeroSuivi(),
                "Votre signalement a bien ete enregistre. Conservez precieusement ce numero de suivi pour consulter son etat."
        );
    }
}
