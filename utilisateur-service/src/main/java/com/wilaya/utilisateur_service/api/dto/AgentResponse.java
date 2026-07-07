package com.wilaya.utilisateur_service.api.dto;

import com.wilaya.utilisateur_service.domain.model.Agent;
import java.util.UUID;

public record AgentResponse(UUID idProfil, UUID idEquipe, String statut) {
    public static AgentResponse depuis(Agent agent) {
        return new AgentResponse(
                agent.getProfil().getIdKeycloak(),
                agent.getIdEquipe(),
                agent.getStatut().name()
        );
    }
}