package com.wilaya.utilisateur_service.infrastructure.persistence;

import com.wilaya.utilisateur_service.domain.model.DemandeReinitialisation;
import com.wilaya.utilisateur_service.domain.model.StatutDemande;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DemandeReinitialisationRepositoryAdapterTest {

    private DemandeReinitialisationJpaRepository jpaRepository;
    private DemandeReinitialisationRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        jpaRepository = mock(DemandeReinitialisationJpaRepository.class);
        adapter = new DemandeReinitialisationRepositoryAdapter(jpaRepository);
    }

    @Test
    void save_doitPersisterEntiteEtRetournerDemande() {
        DemandeReinitialisation demande = DemandeReinitialisation.creer(UUID.randomUUID(), "123456");

        DemandeReinitialisation resultat = adapter.save(demande);

        verify(jpaRepository, times(1)).save(any(DemandeReinitialisationJpaEntity.class));
        assertEquals(demande, resultat);
    }

    @Test
    void findEnAttenteParUtilisateur_doitRetournerDemandeSiTrouvee() {
        UUID idUtilisateur = UUID.randomUUID();
        String code = "123456";

        DemandeReinitialisationJpaEntity entity = new DemandeReinitialisationJpaEntity(
                UUID.randomUUID(), idUtilisateur, code,
                LocalDateTime.now(), LocalDateTime.now().plusMinutes(15),
                StatutDemande.EN_ATTENTE
        );

        when(jpaRepository.findByIdUtilisateurAndCodeAndStatut(idUtilisateur, code, StatutDemande.EN_ATTENTE))
                .thenReturn(Optional.of(entity));

        Optional<DemandeReinitialisation> resultat = adapter.findEnAttenteParUtilisateur(idUtilisateur, code);

        assertTrue(resultat.isPresent());
        assertEquals(idUtilisateur, resultat.get().getIdUtilisateur());
        assertEquals(code, resultat.get().getCode());
    }

    @Test
    void findEnAttenteParUtilisateur_doitRetournerVideSiAucuneDemande() {
        UUID idUtilisateur = UUID.randomUUID();
        String code = "999999";

        when(jpaRepository.findByIdUtilisateurAndCodeAndStatut(idUtilisateur, code, StatutDemande.EN_ATTENTE))
                .thenReturn(Optional.empty());

        Optional<DemandeReinitialisation> resultat = adapter.findEnAttenteParUtilisateur(idUtilisateur, code);

        assertFalse(resultat.isPresent());
    }

    @Test
    void compterDemandesRecentes_doitRetournerNombreDemandes() {
        UUID idUtilisateur = UUID.randomUUID();
        Duration fenetre = Duration.ofMinutes(10);

        DemandeReinitialisationJpaEntity e1 = new DemandeReinitialisationJpaEntity(
                UUID.randomUUID(), idUtilisateur, "111111",
                LocalDateTime.now(), LocalDateTime.now().plusMinutes(15), StatutDemande.EN_ATTENTE
        );
        DemandeReinitialisationJpaEntity e2 = new DemandeReinitialisationJpaEntity(
                UUID.randomUUID(), idUtilisateur, "222222",
                LocalDateTime.now(), LocalDateTime.now().plusMinutes(15), StatutDemande.EN_ATTENTE
        );

        when(jpaRepository.findByIdUtilisateurAndDateCreationAfter(eq(idUtilisateur), any(LocalDateTime.class)))
                .thenReturn(List.of(e1, e2));

        long resultat = adapter.compterDemandesRecentes(idUtilisateur, fenetre);

        assertEquals(2, resultat);
    }

    @Test
    void compterDemandesRecentes_doitRetournerZeroSiAucuneDemande() {
        UUID idUtilisateur = UUID.randomUUID();
        Duration fenetre = Duration.ofMinutes(10);

        when(jpaRepository.findByIdUtilisateurAndDateCreationAfter(eq(idUtilisateur), any(LocalDateTime.class)))
                .thenReturn(List.of());

        long resultat = adapter.compterDemandesRecentes(idUtilisateur, fenetre);

        assertEquals(0, resultat);
    }
}
