package com.wilaya.affectation_service.messaging;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EquipeAffecteeEventTest {

    @Test
    void constructeurInitialiseTousLesChamps() {
        UUID idSignalement = UUID.randomUUID();
        UUID idEquipe = UUID.randomUUID();
        LocalDateTime dateAffectation = LocalDateTime.now();

        EquipeAffecteeEvent event = new EquipeAffecteeEvent(idSignalement, idEquipe, dateAffectation);

        assertThat(event.idSignalement()).isEqualTo(idSignalement);
        assertThat(event.idEquipe()).isEqualTo(idEquipe);
        assertThat(event.dateAffectation()).isEqualTo(dateAffectation);
    }

    @Test
    void deuxEventsAvecLesMemesValeursSontEgaux() {
        UUID idSignalement = UUID.randomUUID();
        UUID idEquipe = UUID.randomUUID();
        LocalDateTime dateAffectation = LocalDateTime.now();

        EquipeAffecteeEvent event1 = new EquipeAffecteeEvent(idSignalement, idEquipe, dateAffectation);
        EquipeAffecteeEvent event2 = new EquipeAffecteeEvent(idSignalement, idEquipe, dateAffectation);

        assertThat(event1).isEqualTo(event2);
        assertThat(event1.hashCode()).isEqualTo(event2.hashCode());
    }

    @Test
    void deuxEventsAvecDesValeursDifferentesNeSontPasEgaux() {
        EquipeAffecteeEvent event1 = new EquipeAffecteeEvent(
                UUID.randomUUID(), UUID.randomUUID(), LocalDateTime.now());
        EquipeAffecteeEvent event2 = new EquipeAffecteeEvent(
                UUID.randomUUID(), UUID.randomUUID(), LocalDateTime.now());

        assertThat(event1).isNotEqualTo(event2);
    }

    @Test
    void toStringContientLesValeursDesChamps() {
        UUID idSignalement = UUID.randomUUID();
        UUID idEquipe = UUID.randomUUID();
        LocalDateTime dateAffectation = LocalDateTime.now();

        EquipeAffecteeEvent event = new EquipeAffecteeEvent(idSignalement, idEquipe, dateAffectation);

        assertThat(event.toString())
                .contains(idSignalement.toString())
                .contains(idEquipe.toString());
    }
}