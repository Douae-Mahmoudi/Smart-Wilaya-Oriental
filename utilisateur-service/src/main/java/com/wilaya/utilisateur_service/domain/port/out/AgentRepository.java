package com.wilaya.utilisateur_service.domain.port.out;

import com.wilaya.utilisateur_service.domain.model.Agent;
import java.util.List;
import java.util.UUID;

public interface AgentRepository {
    Agent save(Agent agent);
    List<Agent> findByIdEquipe(UUID idEquipe);
}





