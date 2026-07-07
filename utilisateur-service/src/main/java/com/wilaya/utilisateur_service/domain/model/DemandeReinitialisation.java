package com.wilaya.utilisateur_service.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;


public class DemandeReinitialisation {

    private static final int DUREE_VALIDITE_MINUTES = 15;

    private final UUID id;
    private final UUID idUtilisateur;
    private final String code;
    private final LocalDateTime dateCreation;
    private final LocalDateTime dateExpiration;
    private StatutDemande statut;

    private DemandeReinitialisation(UUID idUtilisateur, String code) {
        this.id = UUID.randomUUID();
        this.idUtilisateur = idUtilisateur;
        this.code = code;
        this.dateCreation = LocalDateTime.now();
        this.dateExpiration = dateCreation.plusMinutes(DUREE_VALIDITE_MINUTES);
        this.statut = StatutDemande.EN_ATTENTE;
    }

    public static DemandeReinitialisation creer(UUID idUtilisateur, String codeGenere) {
        return new DemandeReinitialisation(idUtilisateur, codeGenere);
    }


    public void verifierCode(String codeSaisi) {
        if (statut != StatutDemande.EN_ATTENTE) {
            throw new IllegalStateException("Ce code a déjà été utilisé ou a expiré");
        }
        if (LocalDateTime.now().isAfter(dateExpiration)) {
            this.statut = StatutDemande.EXPIRE;
            throw new IllegalStateException("Ce code a expiré");
        }
        if (!this.code.equals(codeSaisi)) {
            throw new IllegalArgumentException("Code invalide");
        }
        this.statut = StatutDemande.UTILISE;
    }

    public boolean estEnAttente() {
        return statut == StatutDemande.EN_ATTENTE && LocalDateTime.now().isBefore(dateExpiration);
    }

    public UUID getId() { return id; }
    public UUID getIdUtilisateur() { return idUtilisateur; }
    public String getCode() { return code; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public LocalDateTime getDateExpiration() { return dateExpiration; }
    public StatutDemande getStatut() { return statut; }
}
