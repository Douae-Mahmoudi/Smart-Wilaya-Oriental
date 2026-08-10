package com.wilaya.utilisateur_service.infrastructure.persistence;

import com.wilaya.utilisateur_service.domain.model.Agent;
import com.wilaya.utilisateur_service.domain.model.ProfilUtilisateur;
import com.wilaya.utilisateur_service.domain.port.out.AgentRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class AgentRepositoryAdapter implements AgentRepository {

    private final AgentJpaRepository jpaRepository;
    private final ProfilUtilisateurJpaRepository profilJpaRepository;

    public AgentRepositoryAdapter(AgentJpaRepository jpaRepository,
                                  ProfilUtilisateurJpaRepository profilJpaRepository) {
        this.jpaRepository = jpaRepository;
        this.profilJpaRepository = profilJpaRepository;
    }

    @Override
    public Agent save(Agent agent) {
        AgentJpaEntity entity = new AgentJpaEntity(
                agent.getProfil().getIdKeycloak(), agent.getIdEquipe(), agent.getStatut()
        );
        jpaRepository.save(entity);
        return agent;
    }

    @Override
    public List<Agent> findByIdEquipe(UUID idEquipe) {
        return jpaRepository.findByIdEquipe(idEquipe).stream()
                .map(this::versDomaine)
                .toList();
    }

    @Override
    public List<Agent> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::versDomaine)
                .toList();
    }

    @Override
    public Optional<Agent> findByIdProfil(UUID idProfil) {
        return jpaRepository.findById(idProfil).map(this::versDomaine);
    }

    @Override
    public void deleteByIdProfil(UUID idProfil) {
        if (jpaRepository.existsById(idProfil)) {
            jpaRepository.deleteById(idProfil);
        }
    }

    private Agent versDomaine(AgentJpaEntity entity) {
        ProfilUtilisateur profil = profilJpaRepository.findById(entity.getIdProfil())
                .map(p -> new ProfilUtilisateur(
                        p.getIdKeycloak(), p.getNom(), p.getPrenom(), p.getTelephone(), p.getEmail(), p.getRole()))
                .orElseThrow(() -> new IllegalStateException("Profil introuvable pour l'agent " + entity.getIdProfil()));

        return new Agent(profil, entity.getIdEquipe());
    }
}