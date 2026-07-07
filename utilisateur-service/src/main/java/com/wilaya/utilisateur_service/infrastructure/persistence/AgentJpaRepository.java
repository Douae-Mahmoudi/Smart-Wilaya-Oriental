package com.wilaya.utilisateur_service.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface AgentJpaRepository extends JpaRepository<AgentJpaEntity, UUID> {
    List<AgentJpaEntity> findByIdEquipe(UUID idEquipe);
}