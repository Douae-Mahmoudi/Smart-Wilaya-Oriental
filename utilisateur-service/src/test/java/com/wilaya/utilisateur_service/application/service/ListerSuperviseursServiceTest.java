package com.wilaya.utilisateur_service.application.service;

import com.wilaya.utilisateur_service.domain.model.Agent;
import com.wilaya.utilisateur_service.domain.model.ProfilUtilisateur;
import com.wilaya.utilisateur_service.domain.port.out.ProfilUtilisateurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListerSuperviseursServiceTest {

    @Mock
    private ProfilUtilisateurRepository profilRepository;

    private ListerSuperviseursService service;

    @BeforeEach
    void setUp() {
        service = new ListerSuperviseursService(profilRepository);
    }

    @Test
    void listerSuperviseurs_doitRetournerLaListeDesAgentsPourLesProfilsSuperviseur() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        ProfilUtilisateur sup1 = new ProfilUtilisateur(id1, "Super", "One", "0611111111", "sup1@example.com", "SUPERVISEUR");
        ProfilUtilisateur sup2 = new ProfilUtilisateur(id2, "Super", "Two", "0622222222", "sup2@example.com", "SUPERVISEUR");
        List<ProfilUtilisateur> superviseurs = List.of(sup1, sup2);

        when(profilRepository.findAllByRole("SUPERVISEUR")).thenReturn(superviseurs);

        List<Agent> result = service.listerSuperviseurs();

        assertThat(result)
                .hasSize(2)
                .allMatch(agent -> agent.getIdEquipe() == null)
                .extracting(Agent::getProfil)
                .containsExactly(sup1, sup2);

        verify(profilRepository).findAllByRole("SUPERVISEUR");
    }

    @Test
    void listerSuperviseurs_doitRetournerUneListeVideSiAucunProfilSuperviseur() {
        when(profilRepository.findAllByRole("SUPERVISEUR")).thenReturn(List.of());

        List<Agent> result = service.listerSuperviseurs();

        assertThat(result).isEmpty();
        verify(profilRepository).findAllByRole("SUPERVISEUR");
    }
}