package com.wilaya.affectation_service.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EquipeAffecteePublisher {

    private final RabbitTemplate rabbitTemplate;
    private final String exchange;
    private final String routingKey;

    public EquipeAffecteePublisher(
            RabbitTemplate rabbitTemplate,
            @Value("${rabbitmq.exchange.affectation}") String exchange,
            @Value("${rabbitmq.routingkey.equipe-affectee}") String routingKey) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
        this.routingKey = routingKey;
    }

    public void publier(EquipeAffecteeEvent event) {
        rabbitTemplate.convertAndSend(exchange, routingKey, event);
    }
}