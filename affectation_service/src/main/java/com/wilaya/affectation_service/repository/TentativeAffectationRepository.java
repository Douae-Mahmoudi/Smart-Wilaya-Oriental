package com.wilaya.affectation_service.repository;

import com.wilaya.affectation_service.model.StatutTentative;
import com.wilaya.affectation_service.model.TentativeAffectation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TentativeAffectationRepository extends JpaRepository<TentativeAffectation, UUID> {

    List<TentativeAffectation> findByIdSignalement(UUID idSignalement);

    List<TentativeAffectation> findByStatut(StatutTentative statut);

    List<TentativeAffectation> findByIdSignalementAndStatut(UUID idSignalement, StatutTentative statut);
}