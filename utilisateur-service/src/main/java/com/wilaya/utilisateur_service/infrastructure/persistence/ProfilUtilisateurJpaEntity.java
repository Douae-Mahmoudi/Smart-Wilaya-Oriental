package com.wilaya.utilisateur_service.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "profil_utilisateur")
public class ProfilUtilisateurJpaEntity {

    @Id
    @Column(name = "id_keycloak")
    private UUID idKeycloak;

    private String nom;
    private String prenom;
    private String telephone;

    @Column(unique = true)
    private String email;

    @Column(name = "notifications_activees")
    private boolean notificationsActivees;

    protected ProfilUtilisateurJpaEntity() {
    }

    public ProfilUtilisateurJpaEntity(UUID idKeycloak, String nom, String prenom, String telephone,
                                       String email, boolean notificationsActivees) {
        this.idKeycloak = idKeycloak;
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
        this.email = email;
        this.notificationsActivees = notificationsActivees;
    }

    public UUID getIdKeycloak() { return idKeycloak; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getTelephone() { return telephone; }
    public String getEmail() { return email; }
    public boolean isNotificationsActivees() { return notificationsActivees; }
}
