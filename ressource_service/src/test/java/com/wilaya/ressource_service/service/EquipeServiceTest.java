package com.wilaya.ressource_service.service;

import com.wilaya.ressource_service.dto.CreerEquipeRequest;
import com.wilaya.ressource_service.dto.EquipeDisponibleResponse;
import com.wilaya.ressource_service.exception.RessourceNonTrouveeException;
import com.wilaya.ressource_service.model.CategorieIntervention;
import com.wilaya.ressource_service.model.Equipe;
import com.wilaya.ressource_service.model.Materiel;
import com.wilaya.ressource_service.model.StatutEquipe;
import com.wilaya.ressource_service.repository.EquipeRepository;
import com.wilaya.ressource_service.repository.MaterielRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EquipeServiceTest {

    @Mock
    private EquipeRepository equipeRepository;

    @Mock
    private MaterielRepository materielRepository;

    @InjectMocks
    private EquipeService equipeService;

    private UUID idEquipe;
    private Equipe equipeExistante;

    @BeforeEach
    void setUp() {
        idEquipe = UUID.randomUUID();
        equipeExistante = new Equipe("Equipe Eau Nord", List.of(CategorieIntervention.EAU), "ZoneNord");
    }

    @Test
    void creerEquipe_devrait_sauvegarder_et_retourner_lequipe() {
        CreerEquipeRequest request = new CreerEquipeRequest(
                "Equipe Eau Nord", List.of(CategorieIntervention.EAU), "ZoneNord");

        when(equipeRepository.save(any(Equipe.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Equipe resultat = equipeService.creerEquipe(request);

        assertThat(resultat.getNom()).isEqualTo("Equipe Eau Nord");
        assertThat(resultat.getZoneCouverture()).isEqualTo("ZoneNord");
        assertThat(resultat.getCompetences()).containsExactly(CategorieIntervention.EAU);
        verify(equipeRepository, times(1)).save(any(Equipe.class));
    }

    @Test
    void trouverDisponibles_devrait_indiquer_materielDisponible_true_si_au_moins_un_materiel_libre() {
        when(equipeRepository.findDisponiblesParCompetenceEtZone(
                CategorieIntervention.EAU, "ZoneNord", StatutEquipe.DISPONIBLE))
                .thenReturn(List.of(equipeExistante));

        Materiel materielHorsService = mock(Materiel.class);
        when(materielHorsService.estDisponible()).thenReturn(false);

        Materiel materielDisponible = mock(Materiel.class);
        when(materielDisponible.estDisponible()).thenReturn(true);

        when(materielRepository.findByIdEquipeAssociee(equipeExistante.getId()))
                .thenReturn(List.of(materielHorsService, materielDisponible));

        List<EquipeDisponibleResponse> resultats =
                equipeService.trouverDisponibles(CategorieIntervention.EAU, "ZoneNord");

        assertThat(resultats).hasSize(1);
        verify(materielRepository, times(1)).findByIdEquipeAssociee(equipeExistante.getId());
    }

    @Test
    void trouverDisponibles_devrait_retourner_liste_vide_si_aucune_equipe_disponible() {
        when(equipeRepository.findDisponiblesParCompetenceEtZone(
                CategorieIntervention.ELECTRICITE, "ZoneSud", StatutEquipe.DISPONIBLE))
                .thenReturn(List.of());

        List<EquipeDisponibleResponse> resultats =
                equipeService.trouverDisponibles(CategorieIntervention.ELECTRICITE, "ZoneSud");

        assertThat(resultats).isEmpty();
        verifyNoInteractions(materielRepository);
    }

    @Test
    void changerStatut_devrait_mettre_a_jour_le_statut_si_equipe_existe() {
        when(equipeRepository.findById(idEquipe)).thenReturn(Optional.of(equipeExistante));
        when(equipeRepository.save(any(Equipe.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Equipe resultat = equipeService.changerStatut(idEquipe, StatutEquipe.EN_INTERVENTION);

        assertThat(resultat.getStatut()).isEqualTo(StatutEquipe.EN_INTERVENTION);
        verify(equipeRepository).save(equipeExistante);
    }

    @Test
    void changerStatut_devrait_lever_exception_si_equipe_introuvable() {
        when(equipeRepository.findById(idEquipe)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> equipeService.changerStatut(idEquipe, StatutEquipe.HORS_SERVICE))
                .isInstanceOf(RessourceNonTrouveeException.class)
                .hasMessageContaining("Équipe introuvable");

        verify(equipeRepository, never()).save(any());
    }
}