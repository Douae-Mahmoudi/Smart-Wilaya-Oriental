package com.wilaya.signalement_service.messaging;

import com.wilaya.signalement_service.service.SignalementService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class EquipeAffecteeConsumerTest {

    @Mock
    private SignalementService signalementService;

    @InjectMocks
    private EquipeAffecteeConsumer consumer;

    @Test
    void recevoirEquipeAffectee_devraitAppelerMarquerAffecte_avecIdSignalement() {
        UUID idSignalement = UUID.randomUUID();
        UUID idEquipe = UUID.randomUUID();
        EquipeAffecteeEvent event = new EquipeAffecteeEvent(idSignalement, idEquipe, LocalDateTime.now());

        consumer.recevoirEquipeAffectee(event);

        verify(signalementService).marquerAffecte(idSignalement);
        verifyNoMoreInteractions(signalementService);
    }
}















































































