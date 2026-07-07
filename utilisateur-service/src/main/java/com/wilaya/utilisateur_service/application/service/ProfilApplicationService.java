package com.wilaya.utilisateur_service.application.service;

import com.wilaya.utilisateur_service.domain.model.Agent;
import com.wilaya.utilisateur_service.domain.port.in.ChangerMotDePasseUseCase;
import com.wilaya.utilisateur_service.domain.port.in.ListerAgentsUseCase;
import com.wilaya.utilisateur_service.domain.port.in.ModifierProfilUseCase;
import com.wilaya.utilisateur_service.domain.port.out.AgentRepository;
import com.wilaya.utilisateur_service.domain.port.out.IdentiteProviderPort;
import com.wilaya.utilisateur_service.domain.port.out.ProfilUtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class ProfilApplicationService implements ModifierProfilUseCase, ChangerMotDePasseUseCase, ListerAgentsUseCase {

    private final ProfilUtilisateurRepository profilRepository;
    private final IdentiteProviderPort identiteProviderPort;
    private final AgentRepository agentRepository;

    public ProfilApplicationService(ProfilUtilisateurRepository profilRepository,
                                    IdentiteProviderPort identiteProviderPort,
                                    AgentRepository agentRepository) {
        this.profilRepository = profilRepository;
        this.identiteProviderPort = identiteProviderPort;
        this.agentRepository = agentRepository;
    }

    @Override
    @Transactional
    public void modifierProfil(UUID idKeycloak, String nom, String prenom, String telephone, boolean notificationsActivees) {
        var profil = profilRepository.findByIdKeycloak(idKeycloak)
                .orElseThrow(() -> new NoSuchElementException("Profil introuvable"));

        profil.modifierProfil(nom, prenom, telephone);

        if (notificationsActivees) {
            profil.activerNotifications();
        } else {
            profil.desactiverNotifications();
        }

        profilRepository.save(profil);
    }

    @Override
    public void changerMotDePasse(UUID idKeycloak, String ancienMotDePasse, String nouveauMotDePasse) {
        boolean ancienValide = identiteProviderPort.verifierAncienMotDePasse(idKeycloak, ancienMotDePasse);
        if (!ancienValide) {
            throw new IllegalArgumentException("Ancien mot de passe incorrect");
        }
        identiteProviderPort.changerMotDePasse(idKeycloak, nouveauMotDePasse, false);
    }

    @Override
    public List<Agent> listerParEquipe(UUID idEquipe) {
        return agentRepository.findByIdEquipe(idEquipe);
    }
}