package com.wilaya.utilisateur_service.api.dto;

import com.wilaya.utilisateur_service.domain.model.Agent;
import java.util.UUID;

public record AgentResponse(
        UUID idProfil,
        String nom,
        String prenom,
        String email,
        UUID idEquipe,
        String statut
) {
    public static AgentResponse depuis(Agent agent) {
        return new AgentResponse(
                agent.getProfil().getIdKeycloak(),
                agent.getProfil().getNom(),
                agent.getProfil().getPrenom(),
                agent.getProfil().getEmail(),
                agent.getIdEquipe(),
                agent.getStatut().name()
        );
    }
}