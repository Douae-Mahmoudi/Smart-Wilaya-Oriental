package com.wilaya.utilisateur_service.domain.model;

import java.util.UUID;


public class ProfilUtilisateur {

    private final UUID idKeycloak;
    private String nom;
    private String prenom;
    private String telephone;
    private final String email;
    private boolean notificationsActivees;

    public ProfilUtilisateur(UUID idKeycloak, String nom, String prenom, String telephone, String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Email invalide");
        }
        this.idKeycloak = idKeycloak;
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.email = email;
        this.notificationsActivees = true;
    }

    public void modifierProfil(String nom, String prenom, String telephone) {
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
    }

    public void desactiverNotifications() {
        this.notificationsActivees = false;
    }

    public void activerNotifications() {
        this.notificationsActivees = true;
    }

    public UUID getIdKeycloak() { return idKeycloak; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getTelephone() { return telephone; }
    public String getEmail() { return email; }
    public boolean isNotificationsActivees() { return notificationsActivees; }
}
