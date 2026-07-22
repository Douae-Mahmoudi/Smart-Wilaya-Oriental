package com.wilaya.utilisateur_service.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProfilUtilisateurJpaRepository extends JpaRepository<ProfilUtilisateurJpaEntity, UUID> {
    Optional<ProfilUtilisateurJpaEntity> findByEmail(String email);
    List<ProfilUtilisateurJpaEntity> findAllByRole(String role);
}