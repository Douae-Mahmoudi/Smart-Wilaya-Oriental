package com.wilaya.utilisateur_service.domain.model;

import java.util.UUID;

public class ProfilUtilisateur {

    private final UUID idKeycloak;
    private String nom;
    private String prenom;
    private String telephone;
    private final String email;
    private final String role;          // NOUVEAU
    private boolean notificationsActivees;

    // Constructeur avec rôle
    public ProfilUtilisateur(UUID idKeycloak, String nom, String prenom, String telephone, String email, String role) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Email invalide");
        }
        this.idKeycloak = idKeycloak;
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.email = email;
        this.role = role;
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
    public String getRole() { return role; }           // NOUVEAU GETTER
    public boolean isNotificationsActivees() { return notificationsActivees; }
}