package com.wilaya.utilisateur_service.domain.model;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DemandeReinitialisationTest {

    private static final UUID ID_UTILISATEUR = UUID.randomUUID();
    private static final String CODE = "123456";

    @Test
    void creerInitialiseUneDemandeEnAttente() {
        DemandeReinitialisation demande = DemandeReinitialisation.creer(ID_UTILISATEUR, CODE);

        assertThat(demande.getId()).isNotNull();
        assertThat(demande.getIdUtilisateur()).isEqualTo(ID_UTILISATEUR);
        assertThat(demande.getCode()).isEqualTo(CODE);
        assertThat(demande.getStatut()).isEqualTo(StatutDemande.EN_ATTENTE);
        assertThat(demande.estEnAttente()).isTrue();
    }

    @Test
    void creerFixeLaDateExpirationA15MinutesApresLaCreation() {
        DemandeReinitialisation demande = DemandeReinitialisation.creer(ID_UTILISATEUR, CODE);

        assertThat(demande.getDateExpiration())
                .isEqualTo(demande.getDateCreation().plusMinutes(15));
    }

    @Test
    void verifierCodeAvecLeBonCodePasseLeStatutAUtilise() {
        DemandeReinitialisation demande = DemandeReinitialisation.creer(ID_UTILISATEUR, CODE);

        demande.verifierCode(CODE);

        assertThat(demande.getStatut()).isEqualTo(StatutDemande.UTILISE);
        assertThat(demande.estEnAttente()).isFalse();
    }

    @Test
    void verifierCodeAvecUnMauvaisCodeLanceUneException() {
        DemandeReinitialisation demande = DemandeReinitialisation.creer(ID_UTILISATEUR, CODE);

        assertThatThrownBy(() -> demande.verifierCode("000000"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Code invalide");

        assertThat(demande.getStatut()).isEqualTo(StatutDemande.EN_ATTENTE);
    }

    @Test
    void verifierCodeUneDeuxiemeFoisApresUtilisationLanceUneException() {
        DemandeReinitialisation demande = DemandeReinitialisation.creer(ID_UTILISATEUR, CODE);
        demande.verifierCode(CODE);

        assertThatThrownBy(() -> demande.verifierCode(CODE))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Ce code a déjà été utilisé ou a expiré");
    }

    @Test
    void verifierCodeSurUneDemandeExpireeLanceUneExceptionEtMarqueExpire() throws Exception {
        DemandeReinitialisation demande = DemandeReinitialisation.creer(ID_UTILISATEUR, CODE);
        forcerExpiration(demande);

        assertThatThrownBy(() -> demande.verifierCode(CODE))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Ce code a expiré");

        assertThat(demande.getStatut()).isEqualTo(StatutDemande.EXPIRE);
    }

    @Test
    void estEnAttenteRenvoieFauxSiLaDateExpirationEstDepassee() throws Exception {
        DemandeReinitialisation demande = DemandeReinitialisation.creer(ID_UTILISATEUR, CODE);
        forcerExpiration(demande);

        assertThat(demande.estEnAttente()).isFalse();
    }


    private void forcerExpiration(DemandeReinitialisation demande) throws Exception {
        Field dateExpirationField = DemandeReinitialisation.class.getDeclaredField("dateExpiration");
        dateExpirationField.setAccessible(true);
        dateExpirationField.set(demande, LocalDateTime.now().minusMinutes(1));
    }
}















