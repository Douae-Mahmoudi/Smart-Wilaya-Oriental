package com.wilaya.signalement_service.config;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = RabbitMQConfig.class)
class RabbitMQConfigTest {

    @MockBean
    private ConnectionFactory connectionFactory;

    @Autowired
    private ApplicationContext context;

    @Test
    void beansShouldBeCreated() {
        assertThat(context.getBean(TopicExchange.class)).isNotNull();
        assertThat(context.getBean("signalementClassifieQueue", Queue.class)).isNotNull();
        assertThat(context.getBean(Binding.class)).isNotNull();
        assertThat(context.getBean(Jackson2JsonMessageConverter.class)).isNotNull();
        assertThat(context.getBean(RabbitTemplate.class)).isNotNull();
    }
}