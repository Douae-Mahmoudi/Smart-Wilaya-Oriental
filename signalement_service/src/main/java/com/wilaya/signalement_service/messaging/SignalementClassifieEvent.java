package com.wilaya.signalement_service.messaging;

import com.wilaya.signalement_service.model.NiveauGravite;
import com.wilaya.signalement_service.model.TypeIntervention;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;


public class SignalementClassifieEvent implements Serializable {

    private UUID signalementId;
    private String numeroSuivi;
    private TypeIntervention type;
    private String zone;
    private NiveauGravite gravite;
    private LocalDateTime dateClassification;
    private String description;
    private String adresse;

    public SignalementClassifieEvent() {
    }

    public SignalementClassifieEvent(UUID signalementId, String numeroSuivi, TypeIntervention type,
                                     String zone, NiveauGravite gravite, LocalDateTime dateClassification,
                                     String description, String adresse) {
        this.signalementId = signalementId;
        this.numeroSuivi = numeroSuivi;
        this.type = type;
        this.zone = zone;
        this.gravite = gravite;
        this.dateClassification = dateClassification;
        this.description = description;
        this.adresse = adresse;
    }

    public UUID getSignalementId() { return signalementId; }
    public void setSignalementId(UUID signalementId) { this.signalementId = signalementId; }

    public String getNumeroSuivi() { return numeroSuivi; }
    public void setNumeroSuivi(String numeroSuivi) { this.numeroSuivi = numeroSuivi; }

    public TypeIntervention getType() { return type; }
    public void setType(TypeIntervention type) { this.type = type; }

    public String getZone() { return zone; }
    public void setZone(String zone) { this.zone = zone; }

    public NiveauGravite getGravite() { return gravite; }
    public void setGravite(NiveauGravite gravite) { this.gravite = gravite; }

    public LocalDateTime getDateClassification() { return dateClassification; }
    public void setDateClassification(LocalDateTime dateClassification) { this.dateClassification = dateClassification; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
}