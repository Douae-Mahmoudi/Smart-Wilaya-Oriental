package com.wilaya.signalement_service.messaging;

import com.wilaya.signalement_service.model.Signalement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class SignalementEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(SignalementEventPublisher.class);

    public static final String EXCHANGE = "signalements.exchange";
    public static final String ROUTING_KEY_CLASSIFIE = "signalement.classifie";

    private final RabbitTemplate rabbitTemplate;

    public SignalementEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publierSignalementClassifie(Signalement signalement) {
        SignalementClassifieEvent event = new SignalementClassifieEvent(
                signalement.getId(),
                signalement.getNumeroSuivi(),
                signalement.getType(),
                signalement.getZone(),
                signalement.getGravite(),
                signalement.getDateCreation()
        );
        try {
            rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY_CLASSIFIE, event);
            log.info("Evenement SignalementClassifie publie pour {}", signalement.getNumeroSuivi());
        } catch (Exception e) {
            log.error("Echec de publication de l'evenement SignalementClassifie pour {} : {}",
                    signalement.getNumeroSuivi(), e.getMessage(), e);
        }
    }
}
