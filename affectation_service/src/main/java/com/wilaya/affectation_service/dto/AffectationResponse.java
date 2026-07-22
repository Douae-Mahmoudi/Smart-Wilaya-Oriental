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
                tentative.getDateProposition(),
                tentative.getDateExpiration(),
                tentative.getDateReponse()
        );
    }
}