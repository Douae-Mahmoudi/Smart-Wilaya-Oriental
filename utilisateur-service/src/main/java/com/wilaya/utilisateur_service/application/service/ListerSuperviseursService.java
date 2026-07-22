package com.wilaya.utilisateur_service.application.service;

import com.wilaya.utilisateur_service.domain.model.Agent;
import com.wilaya.utilisateur_service.domain.port.in.ListerSuperviseursUseCase;
import com.wilaya.utilisateur_service.domain.port.out.ProfilUtilisateurRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ListerSuperviseursService implements ListerSuperviseursUseCase {

    private final ProfilUtilisateurRepository profilRepository;

    public ListerSuperviseursService(ProfilUtilisateurRepository profilRepository) {
        this.profilRepository = profilRepository;
    }

    @Override
    public List<Agent> listerSuperviseurs() {
        return profilRepository.findAllByRole("SUPERVISEUR").stream()
                .map(profil -> new Agent(profil, null))
                .collect(Collectors.toList());
    }
}