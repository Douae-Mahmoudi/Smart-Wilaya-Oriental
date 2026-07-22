package com.wilaya.utilisateur_service.domain.port.out;

import com.wilaya.utilisateur_service.domain.model.ProfilUtilisateur;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProfilUtilisateurRepository {

    ProfilUtilisateur save(ProfilUtilisateur profil);

    Optional<ProfilUtilisateur> findByIdKeycloak(UUID idKeycloak);
    void deleteById(UUID id);  // <-- AJOUTER CETTE MÉTHODE

    Optional<ProfilUtilisateur> findByEmail(String email);




    List<ProfilUtilisateur> findAllByRole(String role);

}
