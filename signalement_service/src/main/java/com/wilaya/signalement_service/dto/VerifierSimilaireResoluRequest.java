package com.wilaya.signalement_service.dto;

import com.wilaya.signalement_service.model.TypeIntervention;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VerifierSimilaireResoluRequest(
        @NotNull(message = "Le type d'intervention est obligatoire")
        TypeIntervention type,

        @NotBlank(message = "La description est obligatoire")
        String description,

        String zone,

        Double latitude,

        Double longitude
) { }




















































































