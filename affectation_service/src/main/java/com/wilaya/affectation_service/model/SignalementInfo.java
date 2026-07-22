package com.wilaya.affectation_service.model;

public record SignalementInfo(
        java.util.UUID id,
        String categorie,
        String gravite,
        String zone
) {
}