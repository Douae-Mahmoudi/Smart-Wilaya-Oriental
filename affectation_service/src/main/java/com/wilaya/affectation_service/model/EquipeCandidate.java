package com.wilaya.affectation_service.model;

public record EquipeCandidate(
        java.util.UUID id,
        Double distance,
        Integer chargeActuelle,
        Boolean competenceExacte
) {
}