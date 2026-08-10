package com.wilaya.signalement_service.dto;

import com.wilaya.signalement_service.model.TypeIntervention;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreerSignalementRequest(
        @NotBlank(message = "Le CIN est obligatoire")
        String cinDeclarant,

        @NotNull(message = "Le type d'intervention est obligatoire")
        TypeIntervention type,

        @NotBlank(message = "La description est obligatoire")
        String description,

        @NotBlank(message = "La zone est obligatoire")
        String zone,

        String adresse,

        @NotNull(message = "La latitude est obligatoire")
        Double latitude,

        @NotNull(message = "La longitude est obligatoire")
        Double longitude
) { }