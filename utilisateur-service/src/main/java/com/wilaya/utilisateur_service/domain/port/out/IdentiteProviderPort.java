package com.wilaya.utilisateur_service.domain.port.out;

import java.util.Optional;
import java.util.UUID;

public interface IdentiteProviderPort {

    UUID creerCompte(String email, String nom, String prenom, String motDePasseTemporaire, String role);

    void changerMotDePasse(UUID idKeycloak, String nouveauMotDePasse, boolean temporaire);

    boolean verifierAncienMotDePasse(UUID idKeycloak, String ancienMotDePasse);

    Optional<UUID> trouverIdParEmail(String email);

    // NOUVELLE MÉTHODE
    void supprimerUtilisateur(UUID idKeycloak);
}