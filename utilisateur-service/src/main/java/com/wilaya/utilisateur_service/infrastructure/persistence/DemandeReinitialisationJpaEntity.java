package com.wilaya.utilisateur_service.infrastructure.persistence;

import com.wilaya.utilisateur_service.domain.model.StatutDemande;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "demande_reinitialisation")
public class DemandeReinitialisationJpaEntity {

    @Id
    private UUID id;

    @Column(name = "id_utilisateur")
    private UUID idUtilisateur;

    private String code;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    @Column(name = "date_expiration")
    private LocalDateTime dateExpiration;

    @Enumerated(EnumType.STRING)
    private StatutDemande statut;

    protected DemandeReinitialisationJpaEntity() {
    }

    public DemandeReinitialisationJpaEntity(UUID id, UUID idUtilisateur, String code,
                                             LocalDateTime dateCreation, LocalDateTime dateExpiration,
                                             StatutDemande statut) {
        this.id = id;
        this.idUtilisateur = idUtilisateur;
        this.code = code;
        this.dateCreation = dateCreation;
        this.dateExpiration = dateExpiration;
        this.statut = statut;
    }

    public UUID getId() { return id; }
    public UUID getIdUtilisateur() { return idUtilisateur; }
    public String getCode() { return code; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public LocalDateTime getDateExpiration() { return dateExpiration; }
    public StatutDemande getStatut() { return statut; }
}
