package com.wilaya.utilisateur_service.infrastructure.persistence;

import com.wilaya.utilisateur_service.domain.model.ProfilUtilisateur;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProfilUtilisateurRepositoryAdapterTest {

    private ProfilUtilisateurJpaRepository jpaRepository;
    private ProfilUtilisateurRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        jpaRepository = mock(ProfilUtilisateurJpaRepository.class);
        adapter = new ProfilUtilisateurRepositoryAdapter(jpaRepository);
    }

    @Test
    void save_doitPersisterEntiteEtRetournerProfil() {
        UUID idKeycloak = UUID.randomUUID();
        ProfilUtilisateur profil = new ProfilUtilisateur(
                idKeycloak, "Benali", "Karim", "0555123456", "karim.benali@example.com"
        );

        ProfilUtilisateur resultat = adapter.save(profil);

        verify(jpaRepository, times(1)).save(any(ProfilUtilisateurJpaEntity.class));
        assertEquals(profil, resultat);
    }

    @Test
    void findByIdKeycloak_doitRetournerProfilSiTrouve() {
        UUID idKeycloak = UUID.randomUUID();
        ProfilUtilisateurJpaEntity entity = new ProfilUtilisateurJpaEntity(
                idKeycloak, "Benali", "Karim", "0555123456", "karim.benali@example.com", true
        );

        when(jpaRepository.findById(idKeycloak)).thenReturn(Optional.of(entity));

        Optional<ProfilUtilisateur> resultat = adapter.findByIdKeycloak(idKeycloak);

        assertTrue(resultat.isPresent());
        assertEquals(idKeycloak, resultat.get().getIdKeycloak());
        assertEquals("Benali", resultat.get().getNom());
        assertEquals("Karim", resultat.get().getPrenom());
        assertEquals("karim.benali@example.com", resultat.get().getEmail());
    }

    @Test
    void findByIdKeycloak_doitRetournerVideSiAucunProfil() {
        UUID idKeycloak = UUID.randomUUID();

        when(jpaRepository.findById(idKeycloak)).thenReturn(Optional.empty());

        Optional<ProfilUtilisateur> resultat = adapter.findByIdKeycloak(idKeycloak);

        assertFalse(resultat.isPresent());
    }

    @Test
    void findByEmail_doitRetournerProfilSiTrouve() {
        String email = "sara.meziane@example.com";
        ProfilUtilisateurJpaEntity entity = new ProfilUtilisateurJpaEntity(
                UUID.randomUUID(), "Meziane", "Sara", "0555987654", email, false
        );

        when(jpaRepository.findByEmail(email)).thenReturn(Optional.of(entity));

        Optional<ProfilUtilisateur> resultat = adapter.findByEmail(email);

        assertTrue(resultat.isPresent());
        assertEquals(email, resultat.get().getEmail());
        assertEquals("Meziane", resultat.get().getNom());
    }

    @Test
    void findByEmail_doitRetournerVideSiAucunProfil() {
        String email = "inconnu@example.com";

        when(jpaRepository.findByEmail(email)).thenReturn(Optional.empty());

        Optional<ProfilUtilisateur> resultat = adapter.findByEmail(email);

        assertFalse(resultat.isPresent());
    }
}
