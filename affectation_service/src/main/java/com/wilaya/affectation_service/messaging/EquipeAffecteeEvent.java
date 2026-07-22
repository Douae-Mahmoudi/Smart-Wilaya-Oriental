package com.wilaya.affectation_service.messaging;

import java.util.UUID;

public record EquipeAffecteeEvent(
        UUID idSignalement,
        UUID idEquipe,
        java.time.LocalDateTime dateAffectation
) {
}