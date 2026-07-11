package com.wilaya.ressource_service.dto;

import jakarta.validation.constraints.NotBlank;

public record ChangerStatutRequest(
        @NotBlank String statut
) {}