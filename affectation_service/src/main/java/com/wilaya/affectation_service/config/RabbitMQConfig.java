package com.wilaya.affectation_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Doit correspondre EXACTEMENT a SignalementEventPublisher.EXCHANGE
    // et SignalementEventPublisher.ROUTING_KEY_CLASSIFIE cote signalement-service
    public static final String EXCHANGE_SIGNALEMENTS = "signalements.exchange";
    public static final String ROUTING_KEY_CLASSIFIE = "signalement.classifie";

    @Value("${rabbitmq.queue.signalement-classifie}")
    private String queueSignalementClassifie;

    @Value("${rabbitmq.exchange.affectation}")
    private String exchangeAffectation;

    @Value("${rabbitmq.routingkey.equipe-affectee}")
    private String routingKeyEquipeAffectee;

    @Value("${rabbitmq.exchange.notification}")
    private String exchangeNotification;

    @Value("${rabbitmq.routing-key.notification}")
    private String routingKeyNotification;

    // ---- Consommation : événement SignalementClassifie ----

    @Bean
    public TopicExchange signalementsExchange() {
        return new TopicExchange(EXCHANGE_SIGNALEMENTS);
    }

    @Bean
    public Queue signalementClassifieQueue() {
        return new Queue(queueSignalementClassifie, true);
    }

    @Bean
    public Binding bindingSignalementClassifie(Queue signalementClassifieQueue, TopicExchange signalementsExchange) {
        return BindingBuilder.bind(signalementClassifieQueue)
                .to(signalementsExchange)
                .with(ROUTING_KEY_CLASSIFIE);
    }

    // ---- Publication : événement EquipeAffectee ----

    @Bean
    public TopicExchange affectationExchange() {
        return new TopicExchange(exchangeAffectation);
    }

    // ---- Publication : notification vers notification-service ----

    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(exchangeNotification);
    }

    // ---- Communs ----

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }
}