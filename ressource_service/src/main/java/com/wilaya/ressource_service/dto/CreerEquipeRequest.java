package com.wilaya.ressource_service.dto;

import com.wilaya.ressource_service.model.CategorieIntervention;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreerEquipeRequest(
        @NotBlank String nom,
        @NotEmpty List<CategorieIntervention> competences,
        @NotBlank String zoneCouverture
) {}