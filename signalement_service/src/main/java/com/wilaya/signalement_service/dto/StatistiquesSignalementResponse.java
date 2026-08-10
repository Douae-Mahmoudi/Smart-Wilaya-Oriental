package com.wilaya.signalement_service.dto;

public record StatistiquesSignalementResponse(
        long total,
        long enCours,
        long resolus,
        long critiques
) { }