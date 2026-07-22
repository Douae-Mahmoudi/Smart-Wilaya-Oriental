package com.wilaya.utilisateur_service.infrastructure.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "profil_utilisateur")
public class ProfilUtilisateurJpaEntity {

    @Id
    private UUID idKeycloak;
    private String nom;
    private String prenom;
    private String telephone;
    private String email;
    private String role;              // nouveau champ
    private boolean notificationsActivees;

    // Constructeur par défaut (JPA)
    public ProfilUtilisateurJpaEntity() {}

    // Constructeur avec tous les champs
    public ProfilUtilisateurJpaEntity(UUID idKeycloak, String nom, String prenom, String telephone,
                                      String email, String role, boolean notificationsActivees) {
        this.idKeycloak = idKeycloak;
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.email = email;
        this.role = role;
        this.notificationsActivees = notificationsActivees;
    }

    // Getters et setters
    public UUID getIdKeycloak() { return idKeycloak; }
    public void setIdKeycloak(UUID idKeycloak) { this.idKeycloak = idKeycloak; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isNotificationsActivees() { return notificationsActivees; }
    public void setNotificationsActivees(boolean notificationsActivees) { this.notificationsActivees = notificationsActivees; }
}