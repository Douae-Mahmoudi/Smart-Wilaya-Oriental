package com.wilaya.utilisateur_service.infrastructure.persistence;

import com.wilaya.utilisateur_service.domain.model.StatutDemande;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DemandeReinitialisationJpaRepository extends JpaRepository<DemandeReinitialisationJpaEntity, UUID> {

    Optional<DemandeReinitialisationJpaEntity> findByIdUtilisateurAndCodeAndStatut(
            UUID idUtilisateur, String code, StatutDemande statut);

    List<DemandeReinitialisationJpaEntity> findByIdUtilisateurAndDateCreationAfter(
            UUID idUtilisateur, LocalDateTime seuil);
}
