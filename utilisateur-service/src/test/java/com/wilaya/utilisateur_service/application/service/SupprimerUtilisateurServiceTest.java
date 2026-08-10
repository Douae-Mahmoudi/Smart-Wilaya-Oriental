package com.wilaya.utilisateur_service.application.service;

import com.wilaya.utilisateur_service.domain.port.out.AgentRepository;
import com.wilaya.utilisateur_service.domain.port.out.IdentiteProviderPort;
import com.wilaya.utilisateur_service.domain.port.out.ProfilUtilisateurRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.inOrder;

@ExtendWith(MockitoExtension.class)
class SupprimerUtilisateurServiceTest {

    @Mock
    private IdentiteProviderPort identiteProvider;

    @Mock
    private ProfilUtilisateurRepository profilRepository;

    @Mock
    private AgentRepository agentRepository;

    @Test
    void supprimer_doitAppelerLesDepotsDansLeBonOrdre() {
        SupprimerUtilisateurService service = new SupprimerUtilisateurService(identiteProvider, profilRepository, agentRepository);
        UUID id = UUID.randomUUID();

        service.supprimer(id);

        InOrder inOrder = inOrder(agentRepository, identiteProvider, profilRepository);
        inOrder.verify(agentRepository).deleteByIdProfil(id);
        inOrder.verify(identiteProvider).supprimerUtilisateur(id);
        inOrder.verify(profilRepository).deleteById(id);
    }
}