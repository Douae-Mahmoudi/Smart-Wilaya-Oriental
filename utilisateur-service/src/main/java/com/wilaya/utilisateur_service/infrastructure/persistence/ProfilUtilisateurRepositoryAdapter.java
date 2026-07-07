package com.wilaya.utilisateur_service.infrastructure.persistence;

import com.wilaya.utilisateur_service.domain.model.ProfilUtilisateur;
import com.wilaya.utilisateur_service.domain.port.out.ProfilUtilisateurRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;


@Component
public class ProfilUtilisateurRepositoryAdapter implements ProfilUtilisateurRepository {

    private final ProfilUtilisateurJpaRepository jpaRepository;

    public ProfilUtilisateurRepositoryAdapter(ProfilUtilisateurJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ProfilUtilisateur save(ProfilUtilisateur profil) {
        ProfilUtilisateurJpaEntity entity = versEntity(profil);
        jpaRepository.save(entity);
        return profil;
    }

    @Override
    public Optional<ProfilUtilisateur> findByIdKeycloak(UUID idKeycloak) {
        return jpaRepository.findById(idKeycloak).map(this::versDomaine);
    }

    @Override
    public Optional<ProfilUtilisateur> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(this::versDomaine);
    }

    private ProfilUtilisateurJpaEntity versEntity(ProfilUtilisateur profil) {
        return new ProfilUtilisateurJpaEntity(
                profil.getIdKeycloak(), profil.getNom(), profil.getPrenom(),
                profil.getTelephone(), profil.getEmail(), profil.isNotificationsActivees()
        );
    }

    private ProfilUtilisateur versDomaine(ProfilUtilisateurJpaEntity entity) {
        return new ProfilUtilisateur(
                entity.getIdKeycloak(), entity.getNom(), entity.getPrenom(),
                entity.getTelephone(), entity.getEmail()
        );
    }
}
