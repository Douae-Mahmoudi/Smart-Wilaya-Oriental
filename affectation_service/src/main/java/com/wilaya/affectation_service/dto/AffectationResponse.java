package com.wilaya.affectation_service.dto;

import com.wilaya.affectation_service.model.StatutTentative;
import com.wilaya.affectation_service.model.TentativeAffectation;

import java.time.LocalDateTime;
import java.util.UUID;

public record AffectationResponse(
        UUID id,
        UUID idSignalement,
        UUID idEquipeProposee,
        Double score,
        StatutTentative statut,
        String categorie,
        String gravite,
        String zone,
        String description,
        String adresse,
        LocalDateTime dateProposition,
        LocalDateTime dateExpiration,
        LocalDateTime dateReponse
) {
    public static AffectationResponse depuis(TentativeAffectation tentative) {
        return new AffectationResponse(
                tentative.getId(),
                tentative.getIdSignalement(),
                tentative.getIdEquipeProposee(),
                tentative.getScore(),
                tentative.getStatut(),
                tentative.getCategorie(),
                tentative.getGravite(),
                tentative.getZone(),
                tentative.getDescription(),
                tentative.getAdresse(),
                tentative.getDateProposition(),
                tentative.getDateExpiration(),
                tentative.getDateReponse()
        );
    }
}