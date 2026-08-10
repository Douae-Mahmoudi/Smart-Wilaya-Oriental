package com.wilaya.affectation_service.messaging;

import java.time.LocalDateTime;
import java.util.UUID;

public record SignalementClassifieEvent(
        UUID signalementId,
        String numeroSuivi,
        String type,
        String zone,
        String gravite,
        LocalDateTime dateClassification,
        String description,
        String adresse
) {
}