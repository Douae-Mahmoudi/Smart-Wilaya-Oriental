package com.wilaya.utilisateur_service.application.service;

import com.wilaya.utilisateur_service.domain.model.Agent;
import com.wilaya.utilisateur_service.domain.port.in.ListerAdminsUseCase;
import com.wilaya.utilisateur_service.domain.port.out.ProfilUtilisateurRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ListerAdminsService implements ListerAdminsUseCase {

    private final ProfilUtilisateurRepository profilRepository;

    public ListerAdminsService(ProfilUtilisateurRepository profilRepository) {
        this.profilRepository = profilRepository;
    }

    @Override
    public List<Agent> listerAdmins() {
        return profilRepository.findAllByRole("ADMIN").stream()
                .map(profil -> new Agent(profil, null)) // pas d'équipe pour les admins
                .collect(Collectors.toList());
    }
}