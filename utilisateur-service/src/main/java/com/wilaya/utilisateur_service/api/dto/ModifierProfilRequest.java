package com.wilaya.utilisateur_service.api.dto;

import jakarta.validation.constraints.NotBlank;

public record ModifierProfilRequest(
        @NotBlank String nom,
        @NotBlank String prenom,
        @NotBlank String telephone,
        boolean notificationsActivees
) {}