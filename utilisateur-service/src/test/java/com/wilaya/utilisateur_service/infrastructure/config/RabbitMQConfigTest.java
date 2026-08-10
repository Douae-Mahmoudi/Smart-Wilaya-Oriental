package com.wilaya.utilisateur_service.infrastructure.config;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class RabbitMQConfigTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withUserConfiguration(RabbitMQConfig.class)
            .withPropertyValues(
                    "rabbitmq.exchange.notification=test.exchange",
                    "rabbitmq.queue.notification=test.queue",
                    "rabbitmq.routing-key.notification=test.routing"
            )
            .withBean(ConnectionFactory.class, () -> mock(ConnectionFactory.class));

    @Test
    void shouldCreateAllBeansWithCorrectProperties() {
        runner.run(context -> {
            TopicExchange exchange = context.getBean(TopicExchange.class);
            assertThat(exchange).isNotNull();
            assertThat(exchange.getName()).isEqualTo("test.exchange");

            Queue queue = context.getBean(Queue.class);
            assertThat(queue).isNotNull();
            assertThat(queue.getName()).isEqualTo("test.queue");
            assertThat(queue.isDurable()).isTrue();

            Binding binding = context.getBean(Binding.class);
            assertThat(binding).isNotNull();
            assertThat(binding.getExchange()).isEqualTo("test.exchange");
            assertThat(binding.getRoutingKey()).isEqualTo("test.routing");
            assertThat(binding.getDestination()).isEqualTo("test.queue");

            Jackson2JsonMessageConverter converter = context.getBean(Jackson2JsonMessageConverter.class);
            assertThat(converter).isNotNull();

            RabbitTemplate template = context.getBean(RabbitTemplate.class);
            assertThat(template).isNotNull();
            assertThat(template.getMessageConverter()).isInstanceOf(Jackson2JsonMessageConverter.class);
        });
    }
}