package com.wilaya.utilisateur_service.application.service;

import com.wilaya.utilisateur_service.domain.port.in.SupprimerUtilisateurUseCase;
import com.wilaya.utilisateur_service.domain.port.out.IdentiteProviderPort;
import com.wilaya.utilisateur_service.domain.port.out.ProfilUtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class SupprimerUtilisateurService implements SupprimerUtilisateurUseCase {

    private final IdentiteProviderPort identiteProvider;
    private final ProfilUtilisateurRepository profilRepository;

    public SupprimerUtilisateurService(IdentiteProviderPort identiteProvider,
                                       ProfilUtilisateurRepository profilRepository) {
        this.identiteProvider = identiteProvider;
        this.profilRepository = profilRepository;
    }

    @Override
    @Transactional
    public void supprimer(UUID id) {
        // 1. Supprimer dans Keycloak (via le port)
        identiteProvider.supprimerUtilisateur(id);
        // 2. Supprimer le profil local (la méthode deleteById existe car JpaRepository)
        profilRepository.deleteById(id);
        // Si vous avez aussi une table Agent, supprimez-la ici via AgentRepository si nécessaire
    }
}