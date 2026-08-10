package com.wilaya.signalement_service.dto;

import com.wilaya.signalement_service.model.ChangementStatut;
import com.wilaya.signalement_service.model.StatutSignalement;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ChangementStatutDtoTest {

    @Test
    void devrait_exposer_les_valeurs_passees_au_constructeur() {
        LocalDateTime date = LocalDateTime.of(2026, 8, 8, 10, 30);
        ChangementStatutDto dto = new ChangementStatutDto(StatutSignalement.CLASSIFIE, "Classifié automatiquement", date);

        assertThat(dto.nouveauStatut()).isEqualTo(StatutSignalement.CLASSIFIE);
        assertThat(dto.message()).isEqualTo("Classifié automatiquement");
        assertThat(dto.dateChangement()).isEqualTo(date);
    }

    @Test
    void depuis_devrait_copier_les_champs_du_changement_statut() {
        LocalDateTime date = LocalDateTime.of(2026, 8, 8, 14, 0);
        ChangementStatut changement = mock(ChangementStatut.class);
        when(changement.getNouveauStatut()).thenReturn(StatutSignalement.RESOLU);
        when(changement.getMessage()).thenReturn("Intervention terminée");
        when(changement.getDateChangement()).thenReturn(date);

        ChangementStatutDto dto = ChangementStatutDto.depuis(changement);

        assertThat(dto.nouveauStatut()).isEqualTo(StatutSignalement.RESOLU);
        assertThat(dto.message()).isEqualTo("Intervention terminée");
        assertThat(dto.dateChangement()).isEqualTo(date);
    }

    @Test
    void depuis_devrait_gerer_un_message_null() {
        ChangementStatut changement = mock(ChangementStatut.class);
        when(changement.getNouveauStatut()).thenReturn(StatutSignalement.EN_INTERVENTION);
        when(changement.getMessage()).thenReturn(null);
        when(changement.getDateChangement()).thenReturn(LocalDateTime.of(2026, 8, 8, 9, 0));

        ChangementStatutDto dto = ChangementStatutDto.depuis(changement);

        assertThat(dto.message()).isNull();
    }

    @Test
    void deux_instances_avec_les_memes_valeurs_devraient_etre_egales() {
        LocalDateTime date = LocalDateTime.of(2026, 8, 8, 10, 30);
        ChangementStatutDto dto1 = new ChangementStatutDto(StatutSignalement.AFFECTE, "msg", date);
        ChangementStatutDto dto2 = new ChangementStatutDto(StatutSignalement.AFFECTE, "msg", date);

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }
}
