package com.wilaya.utilisateur_service.infrastructure.persistence;

import com.wilaya.utilisateur_service.domain.model.Agent;
import com.wilaya.utilisateur_service.domain.model.StatutAgent;
import com.wilaya.utilisateur_service.domain.port.out.AgentRepository;
import com.wilaya.utilisateur_service.domain.port.out.ProfilUtilisateurRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Repository
public class AgentRepositoryAdapter implements AgentRepository {

    private final AgentJpaRepository jpaRepository;
    private final ProfilUtilisateurRepository profilRepository;

    public AgentRepositoryAdapter(AgentJpaRepository jpaRepository,
                                  ProfilUtilisateurRepository profilRepository) {
        this.jpaRepository = jpaRepository;
        this.profilRepository = profilRepository;
    }

    @Override
    public Agent save(Agent agent) {
        AgentJpaEntity entity = new AgentJpaEntity(
                agent.getProfil().getIdKeycloak(),
                agent.getIdEquipe(),
                agent.getStatut()
        );

        jpaRepository.save(entity);
        return agent;
    }

    @Override
    public List<Agent> findByIdEquipe(UUID idEquipe) {
        return jpaRepository.findByIdEquipe(idEquipe).stream()
                .map(this::versAgent)
                .toList();
    }

    private Agent versAgent(AgentJpaEntity entity) {
        var profil = profilRepository.findByIdKeycloak(entity.getIdProfil())
                .orElseThrow(() -> new NoSuchElementException("Profil introuvable pour agent " + entity.getIdProfil()));

        Agent agent = new Agent(profil, entity.getIdEquipe());
        if (entity.getStatut() == StatutAgent.INACTIF) {
            agent.desactiver();
        }
        return agent;
    }

}





