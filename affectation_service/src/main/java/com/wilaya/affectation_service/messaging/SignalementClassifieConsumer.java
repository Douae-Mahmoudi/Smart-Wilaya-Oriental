package com.wilaya.affectation_service.messaging;

import com.wilaya.affectation_service.service.AffectationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class SignalementClassifieConsumer {

    private final AffectationService affectationService;

    public SignalementClassifieConsumer(AffectationService affectationService) {
        this.affectationService = affectationService;
    }

    @RabbitListener(queues = "${rabbitmq.queue.signalement-classifie}")
    public void ecouter(SignalementClassifieEvent event) {
        affectationService.demarrerAffectation(event);
    }
}