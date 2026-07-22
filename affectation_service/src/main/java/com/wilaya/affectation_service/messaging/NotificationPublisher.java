package com.wilaya.affectation_service.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class NotificationPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final String exchange;
    private final String routingKey;

    public NotificationPublisher(
            RabbitTemplate rabbitTemplate,
            @Value("${rabbitmq.exchange.notification}") String exchange,
            @Value("${rabbitmq.routing-key.notification}") String routingKey) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
        this.routingKey = routingKey;
    }

    public void publier(NotificationEvent event) {
        rabbitTemplate.convertAndSend(exchange, routingKey, event);
    }
}