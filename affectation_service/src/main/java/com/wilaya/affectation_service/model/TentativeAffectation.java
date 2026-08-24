package com.wilaya.affectation_service.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tentatives_affectation")
public class TentativeAffectation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "id_signalement", nullable = false)
    private UUID idSignalement;

    @Column(name = "id_equipe_proposee", nullable = false)
    private UUID idEquipeProposee;

    @Column(name = "id_agent_accepteur")
    private UUID idAgentAccepteur;

    @Column(nullable = false)
    private Double score;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutTentative statut;

    @Column(name = "date_proposition", nullable = false)
    private LocalDateTime dateProposition;

    @Column(name = "date_expiration", nullable = false)
    private LocalDateTime dateExpiration;

    @Column(name = "date_reponse")
    private LocalDateTime dateReponse;

    @Column(name = "categorie")
    private String categorie;

    @Column(name = "gravite")
    private String gravite;

    @Column(name = "zone")
    private String zone;

    @Column(name = "description", length = 2000)
    private String description;

    @Column(name = "adresse")
    private String adresse;

    @Version
    private Long version;

    protected TentativeAffectation() {
    }

    public TentativeAffectation(UUID idSignalement, UUID idEquipeProposee, Double score, int dureeValiditeMinutes,
                                String categorie, String gravite, String zone,
                                String description, String adresse) {
        this.idSignalement = idSignalement;
        this.idEquipeProposee = idEquipeProposee;
        this.score = score;
        this.statut = StatutTentative.EN_ATTENTE;
        this.dateProposition = LocalDateTime.now(java.time.ZoneOffset.UTC);
        this.dateExpiration = this.dateProposition.plusMinutes(dureeValiditeMinutes);
        this.categorie = categorie;
        this.gravite = gravite;
        this.zone = zone;
        this.description = description;
        this.adresse = adresse;
    }

    public void accepter(UUID idAgent) {
        if (this.statut != StatutTentative.EN_ATTENTE) {
            throw new IllegalStateException("Impossible d'accepter une tentative avec le statut " + this.statut);
        }
        this.statut = StatutTentative.ACCEPTEE;
        this.idAgentAccepteur = idAgent;
        this.dateReponse = LocalDateTime.now(java.time.ZoneOffset.UTC);
    }

    public void refuser() {
        if (this.statut != StatutTentative.EN_ATTENTE) {
            throw new IllegalStateException("Impossible de refuser une tentative avec le statut " + this.statut);
        }
        this.statut = StatutTentative.REFUSEE;
        this.dateReponse = LocalDateTime.now(java.time.ZoneOffset.UTC);
    }

    public Boolean estExpiree() {
        return LocalDateTime.now(java.time.ZoneOffset.UTC).isAfter(this.dateExpiration);
    }

    public void marquerExpiree() {
        if (this.statut == StatutTentative.EN_ATTENTE) {
            this.statut = StatutTentative.EXPIREE;
        }
    }

    public UUID getId() { return id; }
    public UUID getIdSignalement() { return idSignalement; }
    public UUID getIdEquipeProposee() { return idEquipeProposee; }
    public UUID getIdAgentAccepteur() { return idAgentAccepteur; }
    public Double getScore() { return score; }
    public StatutTentative getStatut() { return statut; }
    public LocalDateTime getDateProposition() { return dateProposition; }
    public LocalDateTime getDateExpiration() { return dateExpiration; }
    public LocalDateTime getDateReponse() { return dateReponse; }
    public String getCategorie() { return categorie; }
    public String getGravite() { return gravite; }
    public String getZone() { return zone; }
    public String getDescription() { return description; }
    public String getAdresse() { return adresse; }
}














