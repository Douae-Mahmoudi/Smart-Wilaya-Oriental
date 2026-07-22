package com.wilaya.affectation_service.messaging;

import java.util.UUID;

public record NotificationEvent(
        UUID idEquipe,
        UUID idSignalement,
        String message
) {
}