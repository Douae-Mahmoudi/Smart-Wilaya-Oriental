package com.wilaya.signalement_service.service;

import com.wilaya.signalement_service.dto.RapportIAResponse;
import com.wilaya.signalement_service.dto.StatistiquesSignalementResponse;
import com.wilaya.signalement_service.exception.RapportIAIndisponibleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RapportIAServiceTest {

    @Mock
    private SignalementService signalementService;

    @Mock
    private GenerateurIAClient generateurIAClient;

    private RapportIAService service;

    @BeforeEach
    void setUp() {
        service = new RapportIAService(signalementService, generateurIAClient);
    }

    @Test
    void devrait_generer_un_rapport_a_partir_des_statistiques_existantes() {
        StatistiquesSignalementResponse stats = new StatistiquesSignalementResponse(42, 10, 28, 4);
        when(signalementService.calculerStatistiques()).thenReturn(stats);
        when(generateurIAClient.generer(anyString()))
                .thenReturn("Rapport généré : activité stable ce mois-ci.");

        RapportIAResponse resultat = service.genererRapport();

        assertThat(resultat.contenu()).isEqualTo("Rapport généré : activité stable ce mois-ci.");
        assertThat(resultat.dateGeneration()).isNotNull();
    }

    @Test
    void devrait_construire_un_prompt_contenant_les_statistiques_reelles() {
        StatistiquesSignalementResponse stats = new StatistiquesSignalementResponse(42, 10, 28, 4);
        when(signalementService.calculerStatistiques()).thenReturn(stats);
        when(generateurIAClient.generer(anyString())).thenReturn("ok");

        service.genererRapport();

        ArgumentCaptor<String> promptCapture = ArgumentCaptor.forClass(String.class);
        verify(generateurIAClient).generer(promptCapture.capture());
        String prompt = promptCapture.getValue();

        assertThat(prompt).contains("42");
        assertThat(prompt).contains("10");
        assertThat(prompt).contains("28");
        assertThat(prompt).contains("4");
    }

    @Test
    void ne_devrait_appeler_aucune_methode_decriture_car_lecture_seule() {
        StatistiquesSignalementResponse stats = new StatistiquesSignalementResponse(1, 1, 0, 0);
        when(signalementService.calculerStatistiques()).thenReturn(stats);
        when(generateurIAClient.generer(anyString())).thenReturn("ok");

        service.genererRapport();

        verify(signalementService).calculerStatistiques();
        verifyNoMoreInteractions(signalementService);
    }

    @Test
    void devrait_lancer_une_exception_metier_si_lia_est_indisponible() {
        StatistiquesSignalementResponse stats = new StatistiquesSignalementResponse(5, 2, 3, 0);
        when(signalementService.calculerStatistiques()).thenReturn(stats);
        when(generateurIAClient.generer(anyString())).thenThrow(new RuntimeException("Timeout API IA"));

        assertThatThrownBy(() -> service.genererRapport())
                .isInstanceOf(RapportIAIndisponibleException.class);
    }
}
