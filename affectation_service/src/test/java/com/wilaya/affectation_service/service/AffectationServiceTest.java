package com.wilaya.affectation_service.service;

import com.wilaya.affectation_service.client.RessourceServiceClient;
import com.wilaya.affectation_service.exception.AucuneEquipeDisponibleException;
import com.wilaya.affectation_service.exception.RessourceNonTrouveeException;
import com.wilaya.affectation_service.messaging.EquipeAffecteeEvent;
import com.wilaya.affectation_service.messaging.EquipeAffecteePublisher;
import com.wilaya.affectation_service.messaging.NotificationEvent;
import com.wilaya.affectation_service.messaging.NotificationPublisher;
import com.wilaya.affectation_service.messaging.SignalementClassifieEvent;
import com.wilaya.affectation_service.model.EquipeCandidate;
import com.wilaya.affectation_service.model.StatutTentative;
import com.wilaya.affectation_service.model.TentativeAffectation;
import com.wilaya.affectation_service.repository.TentativeAffectationRepository;
import com.wilaya.affectation_service.scoring.CalculateurScore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AffectationServiceTest {

    @Mock
    private TentativeAffectationRepository repository;

    @Mock
    private RessourceServiceClient ressourceServiceClient;

    @Mock
    private CalculateurScore calculateurScore;

    @Mock
    private EquipeAffecteePublisher equipeAffecteePublisher;

    @Mock
    private NotificationPublisher notificationPublisher;

    private AffectationService affectationService;

    @BeforeEach
    void setUp() {
        affectationService = new AffectationService(
                repository, ressourceServiceClient, calculateurScore,
                equipeAffecteePublisher, notificationPublisher);
    }

    // ---------- demarrerAffectation ----------

    @Test
    void demarrerAffectation_devraitCreerTentativeEtPublierNotification_quandEquipeDisponible() {
        // Arrange
        UUID idSignalement = UUID.randomUUID();
        UUID idEquipe = UUID.randomUUID();
        SignalementClassifieEvent event = new SignalementClassifieEvent(
                idSignalement, "SUIVI-001", "Voirie", "Zone Nord", "Elevee", LocalDateTime.now());

        EquipeCandidate candidate = new EquipeCandidate(idEquipe, 2.5, 1, true);

        when(ressourceServiceClient.trouverEquipesDisponibles("Voirie", "Zone Nord"))
                .thenReturn(List.of(candidate));
        when(calculateurScore.calculer(any(EquipeCandidate.class), any()))
                .thenReturn(0.85);

        // Act
        affectationService.demarrerAffectation(event);

        // Assert
        ArgumentCaptor<TentativeAffectation> captor = ArgumentCaptor.forClass(TentativeAffectation.class);
        verify(repository).save(captor.capture());
        TentativeAffectation tentative = captor.getValue();
        assertEquals(idSignalement, tentative.getIdSignalement());
        assertEquals(idEquipe, tentative.getIdEquipeProposee());
        assertEquals(0.85, tentative.getScore());
        assertEquals(StatutTentative.EN_ATTENTE, tentative.getStatut());

        verify(notificationPublisher).publier(new NotificationEvent(
                idEquipe, idSignalement, "Nouvelle intervention proposée pour votre équipe"));
    }

    @Test
    void demarrerAffectation_devraitChoisirEquipeAvecMeilleurScore_quandPlusieursCandidates() {
        // Arrange
        UUID idSignalement = UUID.randomUUID();
        UUID idEquipeFaible = UUID.randomUUID();
        UUID idEquipeForte = UUID.randomUUID();

        SignalementClassifieEvent event = new SignalementClassifieEvent(
                idSignalement, "SUIVI-001", "Voirie", "Zone Nord", "Elevee", LocalDateTime.now());

        EquipeCandidate candidateFaible = new EquipeCandidate(idEquipeFaible, 5.0, 3, false);
        EquipeCandidate candidateForte = new EquipeCandidate(idEquipeForte, 1.0, 0, true);

        when(ressourceServiceClient.trouverEquipesDisponibles("Voirie", "Zone Nord"))
                .thenReturn(List.of(candidateFaible, candidateForte));
        when(calculateurScore.calculer(eq(candidateFaible), any())).thenReturn(0.3);
        when(calculateurScore.calculer(eq(candidateForte), any())).thenReturn(0.9);

        // Act
        affectationService.demarrerAffectation(event);

        // Assert
        ArgumentCaptor<TentativeAffectation> captor = ArgumentCaptor.forClass(TentativeAffectation.class);
        verify(repository).save(captor.capture());
        assertEquals(idEquipeForte, captor.getValue().getIdEquipeProposee());
        assertEquals(0.9, captor.getValue().getScore());
    }

    @Test
    void demarrerAffectation_devraitLeverException_quandAucuneEquipeDisponible() {
        // Arrange
        UUID idSignalement = UUID.randomUUID();
        SignalementClassifieEvent event = new SignalementClassifieEvent(
                idSignalement, "SUIVI-001", "Voirie", "Zone Nord", "Elevee", LocalDateTime.now());

        when(ressourceServiceClient.trouverEquipesDisponibles("Voirie", "Zone Nord"))
                .thenReturn(List.of());

        // Act & Assert
        assertThrows(AucuneEquipeDisponibleException.class,
                () -> affectationService.demarrerAffectation(event));

        verify(repository, never()).save(any());
        verify(notificationPublisher, never()).publier(any());
    }


    @Test
    void accepter_devraitAccepterTentativeEtPublierEvent_quandValide() {
        // Arrange
        UUID idTentative = UUID.randomUUID();
        UUID idEquipe = UUID.randomUUID();
        UUID idSignalement = UUID.randomUUID();

        TentativeAffectation tentative = new TentativeAffectation(
                idSignalement, idEquipe, 0.8, 15, "Voirie", "Elevee", "Zone Nord");

        when(repository.findById(idTentative)).thenReturn(Optional.of(tentative));

        TentativeAffectation resultat = affectationService.accepter(idTentative, idEquipe);

        assertEquals(StatutTentative.ACCEPTEE, resultat.getStatut());
        verify(repository).save(tentative);
        verify(equipeAffecteePublisher).publier(any(EquipeAffecteeEvent.class));
    }

    @Test
    void accepter_devraitLeverException_quandTentativeIntrouvable() {
        // Arrange
        UUID idTentative = UUID.randomUUID();
        UUID idEquipe = UUID.randomUUID();

        when(repository.findById(idTentative)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RessourceNonTrouveeException.class,
                () -> affectationService.accepter(idTentative, idEquipe));

        verify(equipeAffecteePublisher, never()).publier(any());
    }

    @Test
    void accepter_devraitLeverException_quandEquipeNeCorrespondPas() {
        // Arrange
        UUID idTentative = UUID.randomUUID();
        UUID idEquipeProposee = UUID.randomUUID();
        UUID idAutreEquipe = UUID.randomUUID();

        TentativeAffectation tentative = new TentativeAffectation(
                UUID.randomUUID(), idEquipeProposee, 0.8, 15, "Voirie", "Elevee", "Zone Nord");

        when(repository.findById(idTentative)).thenReturn(Optional.of(tentative));

        // Act & Assert
        assertThrows(IllegalStateException.class,
                () -> affectationService.accepter(idTentative, idAutreEquipe));

        verify(repository, never()).save(any());
        verify(equipeAffecteePublisher, never()).publier(any());
    }

    // ---------- refuser ----------

    @Test
    void refuser_devraitRefuserEtDeclencherReaffectation_quandNouvelleEquipeDisponible() {
        // Arrange
        UUID idTentative = UUID.randomUUID();
        UUID idEquipe = UUID.randomUUID();
        UUID idSignalement = UUID.randomUUID();
        UUID idNouvelleEquipe = UUID.randomUUID();

        TentativeAffectation tentative = new TentativeAffectation(
                idSignalement, idEquipe, 0.8, 15, "Voirie", "Elevee", "Zone Nord");

        when(repository.findById(idTentative)).thenReturn(Optional.of(tentative));

        EquipeCandidate nouvelleCandidate = new EquipeCandidate(idNouvelleEquipe, 1.0, 0, true);
        when(ressourceServiceClient.trouverEquipesDisponibles("Voirie", "Zone Nord"))
                .thenReturn(List.of(nouvelleCandidate));
        when(calculateurScore.calculer(any(EquipeCandidate.class), any())).thenReturn(0.7);

        // Act
        TentativeAffectation resultat = affectationService.refuser(idTentative, idEquipe);

        // Assert
        assertEquals(StatutTentative.REFUSEE, resultat.getStatut());
        // save appelé une fois pour la tentative refusée + une fois pour la nouvelle tentative créée
        verify(repository, times(2)).save(any(TentativeAffectation.class));
        verify(notificationPublisher).publier(any(NotificationEvent.class));
    }

    @Test
    void refuser_neDevraitPasEchouer_quandAucuneEquipeDisponiblePourReaffectation() {
        // Arrange
        UUID idTentative = UUID.randomUUID();
        UUID idEquipe = UUID.randomUUID();
        UUID idSignalement = UUID.randomUUID();

        TentativeAffectation tentative = new TentativeAffectation(
                idSignalement, idEquipe, 0.8, 15, "Voirie", "Elevee", "Zone Nord");

        when(repository.findById(idTentative)).thenReturn(Optional.of(tentative));
        when(ressourceServiceClient.trouverEquipesDisponibles("Voirie", "Zone Nord"))
                .thenReturn(List.of());

        // Act
        TentativeAffectation resultat = affectationService.refuser(idTentative, idEquipe);

        // Assert : aucune exception ne remonte, la tentative refusée reste sauvegardée
        assertEquals(StatutTentative.REFUSEE, resultat.getStatut());
        verify(repository, times(1)).save(any(TentativeAffectation.class));
        verify(notificationPublisher, never()).publier(any());
    }

    @Test
    void refuser_devraitLeverException_quandEquipeNeCorrespondPas() {
        // Arrange
        UUID idTentative = UUID.randomUUID();
        UUID idEquipeProposee = UUID.randomUUID();
        UUID idAutreEquipe = UUID.randomUUID();

        TentativeAffectation tentative = new TentativeAffectation(
                UUID.randomUUID(), idEquipeProposee, 0.8, 15, "Voirie", "Elevee", "Zone Nord");

        when(repository.findById(idTentative)).thenReturn(Optional.of(tentative));

        // Act & Assert
        assertThrows(IllegalStateException.class,
                () -> affectationService.refuser(idTentative, idAutreEquipe));

        verify(repository, never()).save(any());
    }

    // ---------- affecterManuellement ----------

    @Test
    void affecterManuellement_devraitCreerTentativeAccepteeEtPublierEvent() {
        // Arrange
        UUID idSignalement = UUID.randomUUID();
        UUID idEquipe = UUID.randomUUID();

        // Act
        TentativeAffectation resultat = affectationService.affecterManuellement(idSignalement, idEquipe);

        // Assert
        assertEquals(StatutTentative.ACCEPTEE, resultat.getStatut());
        assertEquals(idSignalement, resultat.getIdSignalement());
        assertEquals(idEquipe, resultat.getIdEquipeProposee());
        verify(repository).save(resultat);
        verify(equipeAffecteePublisher).publier(any(EquipeAffecteeEvent.class));
    }


    @Test
    void listerEnAttente_devraitRetournerTentativesEnAttente() {
       TentativeAffectation tentative = new TentativeAffectation(
                UUID.randomUUID(), UUID.randomUUID(), 0.5, 15, "Voirie", "Elevee", "Zone Nord");

        when(repository.findByStatut(StatutTentative.EN_ATTENTE)).thenReturn(List.of(tentative));

        List<TentativeAffectation> resultat = affectationService.listerEnAttente();

        assertEquals(1, resultat.size());
        assertEquals(tentative, resultat.get(0));
    }
}



