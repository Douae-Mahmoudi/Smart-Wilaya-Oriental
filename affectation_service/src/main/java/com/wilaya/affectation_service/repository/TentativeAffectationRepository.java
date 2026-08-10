package com.wilaya.affectation_service.repository;

import com.wilaya.affectation_service.model.StatutTentative;
import com.wilaya.affectation_service.model.TentativeAffectation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TentativeAffectationRepository extends JpaRepository<TentativeAffectation, UUID> {

    List<TentativeAffectation> findByIdSignalement(UUID idSignalement);
    List<TentativeAffectation> findByIdEquipeProposee(UUID idEquipe);

    List<TentativeAffectation> findByStatut(StatutTentative statut);

    List<TentativeAffectation> findByIdSignalementAndStatut(UUID idSignalement, StatutTentative statut);

    @Query("SELECT t FROM TentativeAffectation t WHERE t.statut = 'ACCEPTEE' AND t.idAgentAccepteur = :idAgent")
    List<TentativeAffectation> findAccepteesParAgent(@Param("idAgent") UUID idAgent);
}