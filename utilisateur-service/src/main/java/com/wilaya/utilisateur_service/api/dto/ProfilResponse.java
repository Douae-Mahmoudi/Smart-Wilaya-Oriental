package com.wilaya.utilisateur_service.api.dto;

import com.wilaya.utilisateur_service.domain.model.ProfilUtilisateur;

public record ProfilResponse(
        String nom,
        String prenom,
        String email,
        String telephone,
        boolean notificationsActivees
) {
    public static ProfilResponse depuis(ProfilUtilisateur profil) {
        return new ProfilResponse(
                profil.getNom(), profil.getPrenom(), profil.getEmail(),
                profil.getTelephone(), profil.isNotificationsActivees()
        );
    }
}
