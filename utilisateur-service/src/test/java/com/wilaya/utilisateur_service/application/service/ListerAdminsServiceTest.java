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
class ListerAdminsServiceTest {

    @Mock
    private ProfilUtilisateurRepository profilRepository;

    private ListerAdminsService service;

    @BeforeEach
    void setUp() {
        service = new ListerAdminsService(profilRepository);
    }

    @Test
    void listerAdmins_doitRetournerLaListeDesAgentsPourLesProfilsAdmin() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        ProfilUtilisateur admin1 = new ProfilUtilisateur(id1, "Admin", "One", "0611111111", "admin1@example.com", "ADMIN");
        ProfilUtilisateur admin2 = new ProfilUtilisateur(id2, "Admin", "Two", "0622222222", "admin2@example.com", "ADMIN");
        List<ProfilUtilisateur> admins = List.of(admin1, admin2);

        when(profilRepository.findAllByRole("ADMIN")).thenReturn(admins);

        List<Agent> result = service.listerAdmins();


        assertThat(result)
                .hasSize(2)
                .allMatch(agent -> agent.getIdEquipe() == null) // les admins n'ont pas d'équipe
                .extracting(Agent::getProfil)
                .containsExactly(admin1, admin2);

        verify(profilRepository).findAllByRole("ADMIN");
    }

    @Test
    void listerAdmins_doitRetournerUneListeVideSiAucunProfilAdmin() {
        when(profilRepository.findAllByRole("ADMIN")).thenReturn(List.of());

        List<Agent> result = service.listerAdmins();

        assertThat(result).isEmpty();
        verify(profilRepository).findAllByRole("ADMIN");
    }
}