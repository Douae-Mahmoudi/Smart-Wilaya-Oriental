package com.wilaya.signalement_service.messaging;

import com.wilaya.signalement_service.model.NiveauGravite;
import com.wilaya.signalement_service.model.Signalement;
import com.wilaya.signalement_service.model.TypeIntervention;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SignalementEventPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private SignalementEventPublisher eventPublisher;

    @Test
    void testPublierSignalementClassifieSucces() {
        Signalement signalement = new Signalement(
                "AB123456", TypeIntervention.VOIRIE, "Desc", "url", "Zone", NiveauGravite.MOYENNE,"124 Rue Quods"
        );

        eventPublisher.publierSignalementClassifie(signalement);

        ArgumentCaptor<SignalementClassifieEvent> eventCaptor = ArgumentCaptor.forClass(SignalementClassifieEvent.class);

        verify(rabbitTemplate).convertAndSend(
                eq(SignalementEventPublisher.EXCHANGE),
                eq(SignalementEventPublisher.ROUTING_KEY_CLASSIFIE),
                eventCaptor.capture()
        );

        assertEquals(signalement.getNumeroSuivi(), eventCaptor.getValue().getNumeroSuivi());
        assertEquals(signalement.getType(), eventCaptor.getValue().getType());
    }

    @Test
    void testPublierSignalementClassifieErreurNeBloquePas() {
        Signalement signalement = new Signalement(
                "AB123456", TypeIntervention.VOIRIE, "Desc", "url", "Zone", NiveauGravite.MOYENNE,"123 Rue quods"
        );

        doThrow(new RuntimeException("Rabbit error")).when(rabbitTemplate)
                .convertAndSend(anyString(), anyString(), any(SignalementClassifieEvent.class));

        eventPublisher.publierSignalementClassifie(signalement);

        verify(rabbitTemplate, times(1)).convertAndSend(anyString(), anyString(), any(SignalementClassifieEvent.class));
    }
}