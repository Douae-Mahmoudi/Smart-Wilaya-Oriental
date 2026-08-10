package com.wilaya.utilisateur_service.domain.port.in;

import com.wilaya.utilisateur_service.domain.model.Agent;

import java.util.List;
import java.util.UUID;

public interface ListerAgentsUseCase {
    List<Agent> listerParEquipe(UUID idEquipe);
    List<Agent> listerTous();
}