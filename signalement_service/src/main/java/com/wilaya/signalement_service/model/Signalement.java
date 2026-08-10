package com.wilaya.signalement_service.model;

import jakarta.persistence.*;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "signalement")
public class Signalement {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String CARACTERES_SUIVI = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "numero_suivi", unique = true, nullable = false, updatable = false)
    private String numeroSuivi;

    @Column(name = "cin_declarant", nullable = false)
    private String cinDeclarant;

    @Enumerated(EnumType.STRING)
    private TypeIntervention type;

    @Column(length = 2000)
    private String description;

    @Column(name = "photo_url")
    private String photoUrl;

    private String zone;

    private String adresse;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Enumerated(EnumType.STRING)
    private NiveauGravite gravite;

    @Enumerated(EnumType.STRING)
    private StatutSignalement statut;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @OneToMany(mappedBy = "signalement", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChangementStatut> historiqueStatuts = new ArrayList<>();

    @Column(name = "dernier_message", length = 1000)
    private String dernierMessage;

    protected Signalement() {
    }

    public Signalement(String cinDeclarant, TypeIntervention type, String description,
                       String photoUrl, String zone, NiveauGravite gravite, String adresse,
                       Double latitude, Double longitude) {
        this.numeroSuivi = genererNumeroSuivi();
        this.cinDeclarant = cinDeclarant;
        this.type = type;
        this.description = description;
        this.photoUrl = photoUrl;
        this.zone = zone;
        this.gravite = gravite;
        this.adresse = adresse;
        this.latitude = latitude;
        this.longitude = longitude;
        this.statut = StatutSignalement.SIGNALE;
        this.dateCreation = LocalDateTime.now();
    }

    private static String genererNumeroSuivi() {
        StringBuilder suffixe = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            suffixe.append(CARACTERES_SUIVI.charAt(RANDOM.nextInt(CARACTERES_SUIVI.length())));
        }
        String date = LocalDateTime.now().toLocalDate().toString().replace("-", "");
        return "SIG-" + date + "-" + suffixe;
    }

    public void changerStatut(StatutSignalement nouveauStatut) {
        this.statut = nouveauStatut;
    }

    public String masquerCin() {
        if (cinDeclarant == null || cinDeclarant.length() < 4) {
            return "****";
        }
        String debut = cinDeclarant.substring(0, 2);
        String fin = cinDeclarant.substring(cinDeclarant.length() - 2);
        return debut + "*".repeat(Math.max(cinDeclarant.length() - 4, 2)) + fin;
    }

    public void ajouterChangementStatut(StatutSignalement nouveauStatut, String message) {
        ChangementStatut changement = new ChangementStatut(this, nouveauStatut, message);
        this.historiqueStatuts.add(changement);
        this.dernierMessage = message;
    }

    public UUID getId() { return id; }
    public String getNumeroSuivi() { return numeroSuivi; }
    public String getCinDeclarant() { return cinDeclarant; }
    public TypeIntervention getType() { return type; }
    public String getDescription() { return description; }
    public String getPhotoUrl() { return photoUrl; }
    public String getZone() { return zone; }
    public String getAdresse() { return adresse; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public NiveauGravite getGravite() { return gravite; }
    public StatutSignalement getStatut() { return statut; }
    public LocalDateTime getDateCreation() { return dateCreation; }

    public void setAdresse(String adresse) { this.adresse = adresse; }
    public List<ChangementStatut> getHistoriqueStatuts() { return historiqueStatuts; }
    public void setHistoriqueStatuts(List<ChangementStatut> historiqueStatuts) { this.historiqueStatuts = historiqueStatuts; }
    public String getDernierMessage() { return dernierMessage; }
    public void setDernierMessage(String dernierMessage) { this.dernierMessage = dernierMessage; }
}