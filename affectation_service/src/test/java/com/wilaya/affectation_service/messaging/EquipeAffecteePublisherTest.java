package com.wilaya.affectation_service.messaging;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class EquipeAffecteePublisherTest {

    private static final String EXCHANGE = "affectation.exchange";
    private static final String ROUTING_KEY = "equipe.affectee.routing-key";

    @Mock
    private RabbitTemplate rabbitTemplate;

    private EquipeAffecteePublisher publisher;

    @BeforeEach
    void setUp() {
        publisher = new EquipeAffecteePublisher(rabbitTemplate, EXCHANGE, ROUTING_KEY);
    }

    @Test
    void publier_devraitEnvoyerEvenementAvecExchangeEtRoutingKeyCorrects() {
        // Arrange
        EquipeAffecteeEvent event = new EquipeAffecteeEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                LocalDateTime.now()
        );

        // Act
        publisher.publier(event);

        // Assert
        verify(rabbitTemplate).convertAndSend(EXCHANGE, ROUTING_KEY, event);
        verifyNoMoreInteractions(rabbitTemplate);
    }
}

