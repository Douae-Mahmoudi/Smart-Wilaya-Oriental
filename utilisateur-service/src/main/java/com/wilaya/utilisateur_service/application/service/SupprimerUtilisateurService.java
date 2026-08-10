package com.wilaya.utilisateur_service.application.service;

import com.wilaya.utilisateur_service.domain.port.in.SupprimerUtilisateurUseCase;
import com.wilaya.utilisateur_service.domain.port.out.AgentRepository;
import com.wilaya.utilisateur_service.domain.port.out.IdentiteProviderPort;
import com.wilaya.utilisateur_service.domain.port.out.ProfilUtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class SupprimerUtilisateurService implements SupprimerUtilisateurUseCase {

    private final IdentiteProviderPort identiteProvider;
    private final ProfilUtilisateurRepository profilRepository;
    private final AgentRepository agentRepository;

    public SupprimerUtilisateurService(IdentiteProviderPort identiteProvider,
                                       ProfilUtilisateurRepository profilRepository,
                                       AgentRepository agentRepository) {
        this.identiteProvider = identiteProvider;
        this.profilRepository = profilRepository;
        this.agentRepository = agentRepository;
    }

    @Override
    @Transactional
    public void supprimer(UUID id) {
        agentRepository.deleteByIdProfil(id);
        identiteProvider.supprimerUtilisateur(id);
        profilRepository.deleteById(id);
    }
}