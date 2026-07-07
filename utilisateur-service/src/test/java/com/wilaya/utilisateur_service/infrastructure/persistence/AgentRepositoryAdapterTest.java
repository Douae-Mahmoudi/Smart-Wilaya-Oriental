package com.wilaya.utilisateur_service.infrastructure.persistence;

import com.wilaya.utilisateur_service.domain.model.Agent;
import com.wilaya.utilisateur_service.domain.model.ProfilUtilisateur;
import com.wilaya.utilisateur_service.domain.model.StatutAgent;
import com.wilaya.utilisateur_service.domain.port.out.ProfilUtilisateurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AgentRepositoryAdapterTest {

    private AgentJpaRepository jpaRepository;
    private ProfilUtilisateurRepository profilRepository;
    private AgentRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jpaRepository = mock(AgentJpaRepository.class);
        profilRepository = mock(ProfilUtilisateurRepository.class);
        adapter = new AgentRepositoryAdapter(jpaRepository, profilRepository);
    }

    @Test
    void findByIdEquipe_doitRetournerListeAgentsActifs() {
        UUID idEquipe = UUID.randomUUID();
        UUID idProfil = UUID.randomUUID();

        AgentJpaEntity entity = new AgentJpaEntity(idProfil, idEquipe, StatutAgent.ACTIF);
        ProfilUtilisateur profil = new ProfilUtilisateur(idProfil, "Benali", "Karim", "0555123456", "karim@example.com");

        when(jpaRepository.findByIdEquipe(idEquipe)).thenReturn(List.of(entity));
        when(profilRepository.findByIdKeycloak(idProfil)).thenReturn(Optional.of(profil));

        List<Agent> resultat = adapter.findByIdEquipe(idEquipe);

        assertEquals(1, resultat.size());
        assertEquals(StatutAgent.ACTIF, resultat.get(0).getStatut());
    }

    @Test
    void findByIdEquipe_doitDesactiverAgentSiStatutInactif() {
        UUID idEquipe = UUID.randomUUID();
        UUID idProfil = UUID.randomUUID();

        AgentJpaEntity entity = new AgentJpaEntity(idProfil, idEquipe, StatutAgent.INACTIF);
        ProfilUtilisateur profil = new ProfilUtilisateur(idProfil, "Meziane", "Sara", "0555987654", "sara@example.com");

        when(jpaRepository.findByIdEquipe(idEquipe)).thenReturn(List.of(entity));
        when(profilRepository.findByIdKeycloak(idProfil)).thenReturn(Optional.of(profil));

        List<Agent> resultat = adapter.findByIdEquipe(idEquipe);

        assertEquals(1, resultat.size());
        assertEquals(StatutAgent.INACTIF, resultat.get(0).getStatut());
    }

    @Test
    void findByIdEquipe_doitRetournerListeVideSiAucunAgent() {
        UUID idEquipe = UUID.randomUUID();

        when(jpaRepository.findByIdEquipe(idEquipe)).thenReturn(List.of());

        List<Agent> resultat = adapter.findByIdEquipe(idEquipe);

        assertTrue(resultat.isEmpty());
    }

    @Test
    void findByIdEquipe_doitLeverExceptionSiProfilIntrouvable() {
        UUID idEquipe = UUID.randomUUID();
        UUID idProfil = UUID.randomUUID();

        AgentJpaEntity entity = new AgentJpaEntity(idProfil, idEquipe, StatutAgent.ACTIF);

        when(jpaRepository.findByIdEquipe(idEquipe)).thenReturn(List.of(entity));
        when(profilRepository.findByIdKeycloak(idProfil)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> adapter.findByIdEquipe(idEquipe));
    }
}













