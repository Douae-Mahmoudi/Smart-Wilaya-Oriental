package com.wilaya.signalement_service.dto;

import jakarta.validation.constraints.NotBlank;

public record ChangerStatutRequest(
        @NotBlank(message = "Le statut est obligatoire")
        String statut,

        @NotBlank(message = "Un message explicatif est obligatoire")
        String message
) { }