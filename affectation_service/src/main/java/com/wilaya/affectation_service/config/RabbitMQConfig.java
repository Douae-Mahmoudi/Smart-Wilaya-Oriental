package com.wilaya.affectation_service.config;

import com.wilaya.affectation_service.messaging.EquipeAffecteeEvent;
import com.wilaya.affectation_service.messaging.SignalementClassifieEvent;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_SIGNALEMENTS = "signalements.exchange";
    public static final String ROUTING_KEY_CLASSIFIE = "signalement.classifie";

    public static final String TYPE_ID_SIGNALEMENT_CLASSIFIE = "signalementClassifieEvent";

    public static final String TYPE_ID_EQUIPE_AFFECTEE = "equipeAffecteeEvent";

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


    @Bean
    public TopicExchange affectationExchange() {
        return new TopicExchange(exchangeAffectation);
    }


    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(exchangeNotification);
    }


    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();

        DefaultClassMapper classMapper = new DefaultClassMapper();
        classMapper.setTrustedPackages("*");

        classMapper.setIdClassMapping(Map.of(
                TYPE_ID_SIGNALEMENT_CLASSIFIE, SignalementClassifieEvent.class,
                "com.wilaya.signalement_service.messaging.SignalementClassifieEvent", SignalementClassifieEvent.class,
                TYPE_ID_EQUIPE_AFFECTEE, EquipeAffecteeEvent.class
        ));

        converter.setClassMapper(classMapper);
        return converter;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }
}

























