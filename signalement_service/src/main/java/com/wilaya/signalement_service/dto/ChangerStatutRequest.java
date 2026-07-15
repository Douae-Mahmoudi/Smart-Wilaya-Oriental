package com.wilaya.signalement_service.dto;

import jakarta.validation.constraints.NotBlank;

public record ChangerStatutRequest(
        @NotBlank(message = "Le statut est obligatoire")
        String statut
) {
}
