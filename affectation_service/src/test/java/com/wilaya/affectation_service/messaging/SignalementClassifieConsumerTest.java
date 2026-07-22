package com.wilaya.affectation_service.messaging;

import com.wilaya.affectation_service.service.AffectationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class SignalementClassifieConsumerTest {

    @Mock
    private AffectationService affectationService;

    private SignalementClassifieConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new SignalementClassifieConsumer(affectationService);
    }

    @Test
    void ecouter_devraitAppelerDemarrerAffectation_avecEvenementRecu() {
        // Arrange
        SignalementClassifieEvent event = new SignalementClassifieEvent(
                UUID.randomUUID(),
                "SUIVI-2026-001",
                "Voirie",
                "Zone Nord",
                "Elevee",
                LocalDateTime.now()
        );

        // Act
        consumer.ecouter(event);

        // Assert
        verify(affectationService).demarrerAffectation(event);
        verifyNoMoreInteractions(affectationService);
    }
}

