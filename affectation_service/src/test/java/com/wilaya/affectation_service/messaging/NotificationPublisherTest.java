package com.wilaya.affectation_service.messaging;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class NotificationPublisherTest {

    private static final String EXCHANGE = "notification.exchange";
    private static final String ROUTING_KEY = "notification.routing-key";

    @Mock
    private RabbitTemplate rabbitTemplate;

    private NotificationPublisher publisher;

    @BeforeEach
    void setUp() {
        publisher = new NotificationPublisher(rabbitTemplate, EXCHANGE, ROUTING_KEY);
    }

    @Test
    void publier_devraitEnvoyerEvenementAvecExchangeEtRoutingKeyCorrects() {
        NotificationEvent event = new NotificationEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Une équipe vous a été affectée."
        );

        publisher.publier(event);

        verify(rabbitTemplate).convertAndSend(EXCHANGE, ROUTING_KEY, event);
        verifyNoMoreInteractions(rabbitTemplate);
    }
}
