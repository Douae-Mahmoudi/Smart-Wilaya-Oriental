package com.wilaya.ressource_service.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "equipe")
public class Equipe {

    @Id
    @GeneratedValue
    private UUID id;

    private String nom;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "equipe_competences", joinColumns = @JoinColumn(name = "equipe_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "competence")
    private List<CategorieIntervention> competences = new ArrayList<>();

    @Column(name = "zone_couverture")
    private String zoneCouverture;

    @Enumerated(EnumType.STRING)
    private StatutEquipe statut;

    @Version
    private Long version;

    protected Equipe() {
    }

    public Equipe(String nom, List<CategorieIntervention> competences, String zoneCouverture) {
        this.nom = nom;
        this.competences = competences;
        this.zoneCouverture = zoneCouverture;
        this.statut = StatutEquipe.DISPONIBLE;
    }

    public boolean possedeCompetence(CategorieIntervention categorie) {
        return competences.contains(categorie);
    }

    public boolean estDisponiblePour(CategorieIntervention categorie) {
        return statut == StatutEquipe.DISPONIBLE && possedeCompetence(categorie);
    }

    public void changerStatut(StatutEquipe nouveauStatut) {
        if (this.statut == StatutEquipe.EN_INTERVENTION && nouveauStatut == StatutEquipe.EN_INTERVENTION) {
            throw new IllegalStateException("Cette équipe est déjà affectée à une intervention");
        }
        this.statut = nouveauStatut;
    }

    public UUID getId() { return id; }
    public String getNom() { return nom; }
    public List<CategorieIntervention> getCompetences() { return competences; }
    public String getZoneCouverture() { return zoneCouverture; }
    public StatutEquipe getStatut() { return statut; }
    public Long getVersion() { return version; }
}