package com.wilaya.signalement_service.messaging;

import com.wilaya.signalement_service.service.SignalementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class EquipeAffecteeConsumer {

    private static final Logger log = LoggerFactory.getLogger(EquipeAffecteeConsumer.class);
    private final SignalementService signalementService;

    public EquipeAffecteeConsumer(SignalementService signalementService) {
        this.signalementService = signalementService;
    }

    @RabbitListener(queues = "${rabbitmq.queue.equipe-affectee}")
    public void recevoirEquipeAffectee(EquipeAffecteeEvent event) {
        log.info("Réception de l'événement EquipeAffectee pour le signalement {}", event.idSignalement());
        signalementService.marquerAffecte(event.idSignalement());
    }
}