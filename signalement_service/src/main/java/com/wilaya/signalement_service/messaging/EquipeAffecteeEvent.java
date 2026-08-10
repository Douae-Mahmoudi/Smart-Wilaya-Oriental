package com.wilaya.signalement_service.messaging;

import java.util.UUID;
import java.time.LocalDateTime;

public record EquipeAffecteeEvent(
        UUID idSignalement,
        UUID idEquipe,
        LocalDateTime dateAcceptation
) {}