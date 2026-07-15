package com.wilaya.signalement_service.config;

import com.wilaya.signalement_service.messaging.SignalementEventPublisher;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_CLASSIFIE = "signalements.classifie.queue";

    @Bean
    public TopicExchange signalementsExchange() {
        return new TopicExchange(SignalementEventPublisher.EXCHANGE);
    }

    @Bean
    public Queue signalementClassifieQueue() {
        return new Queue(QUEUE_CLASSIFIE, true);
    }

    @Bean
    public Binding bindingSignalementClassifie(Queue signalementClassifieQueue, TopicExchange signalementsExchange) {
        return BindingBuilder.bind(signalementClassifieQueue)
                .to(signalementsExchange)
                .with(SignalementEventPublisher.ROUTING_KEY_CLASSIFIE);
    }

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
