package com.wilaya.ressource_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreerMaterielRequest(
        @NotBlank String type,
        @NotNull UUID idEquipeAssociee
) {}