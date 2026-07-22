package com.wilaya.utilisateur_service.infrastructure.persistence;

import com.wilaya.utilisateur_service.domain.model.StatutAgent;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "agent")
public class AgentJpaEntity {

    @Id
    @Column(name = "id_profil")
    private UUID idProfil;

    @Column(name = "id_equipe")
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
    public UUID getIdEquipe() { return idEquipe; }
    public StatutAgent getStatut() { return statut; }
}