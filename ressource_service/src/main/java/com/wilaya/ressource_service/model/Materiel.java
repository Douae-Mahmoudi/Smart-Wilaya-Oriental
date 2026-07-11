package com.wilaya.ressource_service.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "materiel")
public class Materiel {

    @Id
    @GeneratedValue
    private UUID id;

    private String type;

    @Column(name = "id_equipe_associee")
    private UUID idEquipeAssociee;

    @Enumerated(EnumType.STRING)
    private StatutMateriel statut;

    protected Materiel() {
    }

    public Materiel(String type, UUID idEquipeAssociee) {
        this.type = type;
        this.idEquipeAssociee = idEquipeAssociee;
        this.statut = StatutMateriel.DISPONIBLE;
    }

    public boolean estDisponible() {
        return statut == StatutMateriel.DISPONIBLE;
    }

    public void changerStatut(StatutMateriel nouveauStatut) {
        this.statut = nouveauStatut;
    }

    public UUID getId() { return id; }
    public String getType() { return type; }
    public UUID getIdEquipeAssociee() { return idEquipeAssociee; }
    public StatutMateriel getStatut() { return statut; }
}