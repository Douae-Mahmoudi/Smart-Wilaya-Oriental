package com.wilaya.utilisateur_service.application.service;

import com.wilaya.utilisateur_service.domain.model.Agent;
import com.wilaya.utilisateur_service.domain.model.ProfilUtilisateur;
import com.wilaya.utilisateur_service.domain.port.in.CreerCompteUseCase;
import com.wilaya.utilisateur_service.domain.port.out.EmailSenderPort;
import com.wilaya.utilisateur_service.domain.port.out.IdentiteProviderPort;
import com.wilaya.utilisateur_service.domain.port.out.ProfilUtilisateurRepository;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.UUID;

@Service
public class CompteApplicationService implements CreerCompteUseCase {

    private static final String CARACTERES = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final ProfilUtilisateurRepository profilRepository;
    private final IdentiteProviderPort identiteProvider;
    private final EmailSenderPort emailSender;

    public CompteApplicationService(ProfilUtilisateurRepository profilRepository,
                                    IdentiteProviderPort identiteProvider,
                                    EmailSenderPort emailSender) {
        this.profilRepository = profilRepository;
        this.identiteProvider = identiteProvider;
        this.emailSender = emailSender;
    }

    @Override
    public UUID creerCompte(String nom, String prenom, String email, String telephone,
                            String role, UUID idEquipe) {
        String motDePasseTemporaire = genererMotDePasseTemporaire();

        UUID idKeycloak = identiteProvider.creerCompte(email, nom, prenom, motDePasseTemporaire, role);

        ProfilUtilisateur profil = new ProfilUtilisateur(idKeycloak, nom, prenom, telephone, email);
        profilRepository.save(profil);

        if ("AGENT".equals(role) && idEquipe != null) {
            Agent agent = new Agent(profil, idEquipe);
        }

        emailSender.envoyerIdentifiantsTemporaires(email, email, motDePasseTemporaire);

        return idKeycloak;
    }

    private String genererMotDePasseTemporaire() {
        StringBuilder sb = new StringBuilder(12);
        for (int i = 0; i < 12; i++) {
            sb.append(CARACTERES.charAt(RANDOM.nextInt(CARACTERES.length())));
        }
        return sb.toString();
    }
}