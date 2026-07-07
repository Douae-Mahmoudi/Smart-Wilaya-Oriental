package com.wilaya.utilisateur_service.infrastructure.persistence;

import com.wilaya.utilisateur_service.domain.model.StatutAgent;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "agents")
public class AgentJpaEntity {

    @Id
    private UUID idProfil;

    private UUID idEquipe;

    @Enumerated(EnumType.STRING)
    private StatutAgent statut;

    protected AgentJpaEntity() {
    }

    public AgentJpaEntity(UUID idProfil, UUID idEquipe, StatutAgent statut) {
        this.idProfil = idProfil;
        this.idEquipe = idEquipe;
        this.statut = statut;
    }

    public UUID getIdProfil() { return idProfil; }
    public void setIdProfil(UUID idProfil) { this.idProfil = idProfil; }

    public UUID getIdEquipe() { return idEquipe; }
    public void setIdEquipe(UUID idEquipe) { this.idEquipe = idEquipe; }

    public StatutAgent getStatut() { return statut; }
    public void setStatut(StatutAgent statut) { this.statut = statut; }
}