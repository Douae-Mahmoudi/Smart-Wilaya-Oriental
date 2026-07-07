package com.wilaya.utilisateur_service.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record DemandeResetRequest(
        @NotBlank @Email String email
) {}
