package com.wilaya.affectation_service.service;

import com.wilaya.affectation_service.client.RessourceServiceClient;
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

    private static final UUID AUCUNE_EQUIPE_DISPONIBLE = new UUID(0L, 0L);

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

    private SignalementClassifieEvent creerEvent(UUID idSignalement) {
        return new SignalementClassifieEvent(
                idSignalement, "SUIVI-001", "Voirie", "Zone Nord", "Elevee",
                LocalDateTime.now(), "Description test", "Adresse test");
    }


    @Test
    void demarrerAffectation_devraitCreerTentativeEtPublierNotification_quandEquipeDisponible() {
        UUID idSignalement = UUID.randomUUID();
        UUID idEquipe = UUID.randomUUID();
        SignalementClassifieEvent event = creerEvent(idSignalement);

        EquipeCandidate candidate = new EquipeCandidate(idEquipe, 2.5, 1, true);

        when(ressourceServiceClient.trouverEquipesDisponibles("Voirie", "Zone Nord"))
                .thenReturn(List.of(candidate));
        when(calculateurScore.calculer(any(EquipeCandidate.class), any()))
                .thenReturn(0.85);

        affectationService.demarrerAffectation(event);

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
        UUID idSignalement = UUID.randomUUID();
        UUID idEquipeFaible = UUID.randomUUID();
        UUID idEquipeForte = UUID.randomUUID();

        SignalementClassifieEvent event = creerEvent(idSignalement);

        EquipeCandidate candidateFaible = new EquipeCandidate(idEquipeFaible, 5.0, 3, false);
        EquipeCandidate candidateForte = new EquipeCandidate(idEquipeForte, 1.0, 0, true);

        when(ressourceServiceClient.trouverEquipesDisponibles("Voirie", "Zone Nord"))
                .thenReturn(List.of(candidateFaible, candidateForte));
        when(calculateurScore.calculer(eq(candidateFaible), any())).thenReturn(0.3);
        when(calculateurScore.calculer(eq(candidateForte), any())).thenReturn(0.9);

        affectationService.demarrerAffectation(event);

        ArgumentCaptor<TentativeAffectation> captor = ArgumentCaptor.forClass(TentativeAffectation.class);
        verify(repository).save(captor.capture());
        assertEquals(idEquipeForte, captor.getValue().getIdEquipeProposee());
        assertEquals(0.9, captor.getValue().getScore());
    }

    @Test
    void demarrerAffectation_devraitCreerPlaceholderRefuse_quandAucuneEquipeDisponible() {
        UUID idSignalement = UUID.randomUUID();
        SignalementClassifieEvent event = creerEvent(idSignalement);

        when(ressourceServiceClient.trouverEquipesDisponibles("Voirie", "Zone Nord"))
                .thenReturn(List.of());

        affectationService.demarrerAffectation(event);

        ArgumentCaptor<TentativeAffectation> captor = ArgumentCaptor.forClass(TentativeAffectation.class);
        verify(repository).save(captor.capture());
        TentativeAffectation placeholder = captor.getValue();
        assertEquals(idSignalement, placeholder.getIdSignalement());
        assertEquals(AUCUNE_EQUIPE_DISPONIBLE, placeholder.getIdEquipeProposee());
        assertEquals(StatutTentative.REFUSEE, placeholder.getStatut());

        verify(notificationPublisher, never()).publier(any());
    }


    @Test
    void accepter_devraitAccepterTentativeEtPublierEvent_quandValide() {
        UUID idTentative = UUID.randomUUID();
        UUID idEquipe = UUID.randomUUID();
        UUID idSignalement = UUID.randomUUID();
        UUID idAgent = UUID.randomUUID();

        TentativeAffectation tentative = new TentativeAffectation(
                idSignalement, idEquipe, 0.8, 15, "Voirie", "Elevee", "Zone Nord",
                "Description test", "Adresse test");

        when(repository.findById(idTentative)).thenReturn(Optional.of(tentative));

        TentativeAffectation resultat = affectationService.accepter(idTentative, idEquipe, idAgent);

        assertEquals(StatutTentative.ACCEPTEE, resultat.getStatut());
        verify(repository).save(tentative);
        verify(equipeAffecteePublisher).publier(any(EquipeAffecteeEvent.class));
    }

    @Test
    void accepter_devraitLeverException_quandTentativeIntrouvable() {
        UUID idTentative = UUID.randomUUID();
        UUID idEquipe = UUID.randomUUID();
        UUID idAgent = UUID.randomUUID();

        when(repository.findById(idTentative)).thenReturn(Optional.empty());

        assertThrows(RessourceNonTrouveeException.class,
                () -> affectationService.accepter(idTentative, idEquipe, idAgent));

        verify(equipeAffecteePublisher, never()).publier(any());
    }

    @Test
    void accepter_devraitLeverException_quandEquipeNeCorrespondPas() {
        UUID idTentative = UUID.randomUUID();
        UUID idEquipeProposee = UUID.randomUUID();
        UUID idAutreEquipe = UUID.randomUUID();
        UUID idAgent = UUID.randomUUID();

        TentativeAffectation tentative = new TentativeAffectation(
                UUID.randomUUID(), idEquipeProposee, 0.8, 15, "Voirie", "Elevee", "Zone Nord",
                "Description test", "Adresse test");

        when(repository.findById(idTentative)).thenReturn(Optional.of(tentative));

        assertThrows(IllegalStateException.class,
                () -> affectationService.accepter(idTentative, idAutreEquipe, idAgent));

        verify(repository, never()).save(any());
        verify(equipeAffecteePublisher, never()).publier(any());
    }


    @Test
    void refuser_devraitRefuserEtDeclencherReaffectation_quandNouvelleEquipeDisponible() {
        UUID idTentative = UUID.randomUUID();
        UUID idEquipe = UUID.randomUUID();
        UUID idSignalement = UUID.randomUUID();
        UUID idNouvelleEquipe = UUID.randomUUID();

        TentativeAffectation tentative = new TentativeAffectation(
                idSignalement, idEquipe, 0.8, 15, "Voirie", "Elevee", "Zone Nord",
                "Description test", "Adresse test");

        when(repository.findById(idTentative)).thenReturn(Optional.of(tentative));
        when(repository.findByIdSignalement(idSignalement)).thenReturn(List.of(tentative));

        EquipeCandidate nouvelleCandidate = new EquipeCandidate(idNouvelleEquipe, 1.0, 0, true);
        when(ressourceServiceClient.trouverEquipesDisponibles("Voirie", "Zone Nord"))
                .thenReturn(List.of(nouvelleCandidate));
        when(calculateurScore.calculer(any(EquipeCandidate.class), any())).thenReturn(0.7);

        TentativeAffectation resultat = affectationService.refuser(idTentative, idEquipe);

        assertEquals(StatutTentative.REFUSEE, resultat.getStatut());
        verify(repository, times(2)).save(any(TentativeAffectation.class));
        verify(notificationPublisher).publier(any(NotificationEvent.class));
    }

    @Test
    void refuser_neDevraitPasEchouer_quandAucuneEquipeDisponiblePourReaffectation() {
        UUID idTentative = UUID.randomUUID();
        UUID idEquipe = UUID.randomUUID();
        UUID idSignalement = UUID.randomUUID();

        TentativeAffectation tentative = new TentativeAffectation(
                idSignalement, idEquipe, 0.8, 15, "Voirie", "Elevee", "Zone Nord",
                "Description test", "Adresse test");

        when(repository.findById(idTentative)).thenReturn(Optional.of(tentative));
        when(repository.findByIdSignalement(idSignalement)).thenReturn(List.of(tentative));
        when(ressourceServiceClient.trouverEquipesDisponibles("Voirie", "Zone Nord"))
                .thenReturn(List.of());

        TentativeAffectation resultat = affectationService.refuser(idTentative, idEquipe);

        assertEquals(StatutTentative.REFUSEE, resultat.getStatut());
        verify(repository, times(1)).save(any(TentativeAffectation.class));
        verify(notificationPublisher, never()).publier(any());
    }

    @Test
    void refuser_devraitLeverException_quandEquipeNeCorrespondPas() {
        UUID idTentative = UUID.randomUUID();
        UUID idEquipeProposee = UUID.randomUUID();
        UUID idAutreEquipe = UUID.randomUUID();

        TentativeAffectation tentative = new TentativeAffectation(
                UUID.randomUUID(), idEquipeProposee, 0.8, 15, "Voirie", "Elevee", "Zone Nord",
                "Description test", "Adresse test");

        when(repository.findById(idTentative)).thenReturn(Optional.of(tentative));

        assertThrows(IllegalStateException.class,
                () -> affectationService.refuser(idTentative, idAutreEquipe));

        verify(repository, never()).save(any());
    }


    @Test
    void affecterManuellement_devraitCreerTentativeEnAttente_sansEquipePrecedente() {
        UUID idSignalement = UUID.randomUUID();
        UUID idEquipe = UUID.randomUUID();

        when(repository.findByIdSignalement(idSignalement)).thenReturn(List.of());

        TentativeAffectation resultat = affectationService.affecterManuellement(idSignalement, idEquipe);

        assertEquals(StatutTentative.EN_ATTENTE, resultat.getStatut());
        assertEquals(idSignalement, resultat.getIdSignalement());
        assertEquals(idEquipe, resultat.getIdEquipeProposee());
        verify(repository).save(resultat);
        verify(equipeAffecteePublisher, never()).publier(any());
    }

    @Test
    void affecterManuellement_devraitReprendreLaCategorieDeLaDerniereTentativeConnue() {
        UUID idSignalement = UUID.randomUUID();
        UUID idAncienneEquipe = UUID.randomUUID();
        UUID idNouvelleEquipe = UUID.randomUUID();

        TentativeAffectation derniereConnue = new TentativeAffectation(
                idSignalement, idAncienneEquipe, 0.5, 15, "Voirie", "Elevee", "Zone Nord",
                "Description test", "Adresse test");

        when(repository.findByIdSignalement(idSignalement)).thenReturn(List.of(derniereConnue));

        TentativeAffectation resultat = affectationService.affecterManuellement(idSignalement, idNouvelleEquipe);

        assertEquals("Voirie", resultat.getCategorie());
        assertEquals("Zone Nord", resultat.getZone());
    }


    @Test
    void listerEnAttente_devraitRetournerTentativesEnAttente() {
        TentativeAffectation tentative = new TentativeAffectation(
                UUID.randomUUID(), UUID.randomUUID(), 0.5, 15, "Voirie", "Elevee", "Zone Nord",
                "Description test", "Adresse test");

        when(repository.findByStatut(StatutTentative.EN_ATTENTE)).thenReturn(List.of(tentative));

        List<TentativeAffectation> resultat = affectationService.listerEnAttente();

        assertEquals(1, resultat.size());
        assertEquals(tentative, resultat.get(0));
    }


    @Test
    void listerPourAgentEtEquipe_devraitInclureLesTentativesEnAttente() {
        UUID idEquipe = UUID.randomUUID();
        UUID idAgent = UUID.randomUUID();
        TentativeAffectation enAttente = new TentativeAffectation(
                UUID.randomUUID(), idEquipe, 0.5, 15, "Voirie", "Elevee", "Zone Nord",
                "Description test", "Adresse test");

        when(repository.findByIdEquipeProposee(idEquipe)).thenReturn(List.of(enAttente));

        List<TentativeAffectation> resultat = affectationService.listerPourAgentEtEquipe(idEquipe, idAgent);

        assertEquals(1, resultat.size());
    }

    @Test
    void listerPourAgentEtEquipe_neDevraitPasInclureUneTentativeAccepteeParUnAutreAgent() {
        UUID idEquipe = UUID.randomUUID();
        UUID idAgentConnecte = UUID.randomUUID();
        UUID idAutreAgent = UUID.randomUUID();

        TentativeAffectation tentative = new TentativeAffectation(
                UUID.randomUUID(), idEquipe, 0.5, 15, "Voirie", "Elevee", "Zone Nord",
                "Description test", "Adresse test");
        tentative.accepter(idAutreAgent);

        when(repository.findByIdEquipeProposee(idEquipe)).thenReturn(List.of(tentative));

        List<TentativeAffectation> resultat = affectationService.listerPourAgentEtEquipe(idEquipe, idAgentConnecte);

        assertEquals(0, resultat.size());
    }
}