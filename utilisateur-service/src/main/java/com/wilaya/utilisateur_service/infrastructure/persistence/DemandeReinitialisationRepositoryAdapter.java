package com.wilaya.utilisateur_service.infrastructure.persistence;

import com.wilaya.utilisateur_service.domain.model.DemandeReinitialisation;
import com.wilaya.utilisateur_service.domain.model.StatutDemande;
import com.wilaya.utilisateur_service.domain.port.out.DemandeReinitialisationRepository;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Component
public class DemandeReinitialisationRepositoryAdapter implements DemandeReinitialisationRepository {

    private final DemandeReinitialisationJpaRepository jpaRepository;

    public DemandeReinitialisationRepositoryAdapter(DemandeReinitialisationJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public DemandeReinitialisation save(DemandeReinitialisation demande) {
        DemandeReinitialisationJpaEntity entity = new DemandeReinitialisationJpaEntity(
                demande.getId(), demande.getIdUtilisateur(), demande.getCode(),
                demande.getDateCreation(), demande.getDateExpiration(), demande.getStatut()
        );
        jpaRepository.save(entity);
        return demande;
    }

    @Override
    public Optional<DemandeReinitialisation> findEnAttenteParUtilisateur(UUID idUtilisateur, String code) {
        return jpaRepository.findByIdUtilisateurAndCodeAndStatut(idUtilisateur, code, StatutDemande.EN_ATTENTE)
                .map(e -> DemandeReinitialisation.creer(e.getIdUtilisateur(), e.getCode()));
    }

    @Override
    public long compterDemandesRecentes(UUID idUtilisateur, Duration fenetre) {
        LocalDateTime seuil = LocalDateTime.now().minus(fenetre);
        return jpaRepository.findByIdUtilisateurAndDateCreationAfter(idUtilisateur, seuil).size();
    }
}
