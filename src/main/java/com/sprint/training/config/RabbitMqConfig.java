package com.sprint.training.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.json.JsonMapper;


@Configuration
public class RabbitMqConfig {

    public static final String EXCHANGE_GYM = "gym.events";
    public static final String QUEUE_NOTIFICATIONS = "gym.notifications.queue";
    public static final String ROUTING_KEY_ACCESS_REGISTERED = "gym.access.registered";
    public static final String BINDING_PATTERN_ACCESS = "gym.access.*";

    @Bean
    public TopicExchange gymExchange() {
        return new TopicExchange(EXCHANGE_GYM);
    }

    @Bean
    public Queue notificationsQueue() {
        return QueueBuilder.durable(QUEUE_NOTIFICATIONS).build();
    }

    @Bean
    public Binding bindingNotifications(Queue notificationsQueue, TopicExchange gymExchange) {
        return BindingBuilder
                .bind(notificationsQueue)
                .to(gymExchange)
                .with(BINDING_PATTERN_ACCESS);
    }

    @Bean
    public MessageConverter jsonMessageConverter(JsonMapper jsonMapper) {
        return new JacksonJsonMessageConverter(jsonMapper);
    }

}
