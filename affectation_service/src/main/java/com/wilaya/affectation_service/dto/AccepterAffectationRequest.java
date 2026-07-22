package com.wilaya.affectation_service.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AccepterAffectationRequest(
        @NotNull UUID idEquipe
) {
}