package com.sprint.training.config;

import com.sprint.training.constants.RabbitConstants;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.json.JsonMapper;


@Configuration
public class RabbitMqConfig {

    //================
    //Main QUEUE
    //================
    @Bean
    public TopicExchange gymExchange() {
        return new TopicExchange(RabbitConstants.EXCHANGE_GYM);
    }

    @Bean
    public Queue notificationsQueue() {
        return QueueBuilder.durable(RabbitConstants.QUEUE_NOTIFICATIONS)
                .withArgument("x-dead-letter-exchange", RabbitConstants.DLX_GYM)
                .withArgument("x-dead-letter-routing-key", RabbitConstants.QUEUE_NOTIFICATIONS)
                .build();
    }

    @Bean
    public Binding bindingNotifications(Queue notificationsQueue, TopicExchange gymExchange) {
        return BindingBuilder
                .bind(notificationsQueue)
                .to(gymExchange)
                .with(RabbitConstants.BINDING_PATTERN_ACCESS);
    }

    //================
    //DLQ
    //================
    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(RabbitConstants.DLX_GYM);
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(RabbitConstants.QUEUE_NOTIFICATIONS_DLQ).build();
    }

    @Bean
    public Binding deadLetterBinding(Queue deadLetterQueue, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange).with(RabbitConstants.QUEUE_NOTIFICATIONS);
    }

    @Bean
    public MessageConverter jsonMessageConverter(JsonMapper jsonMapper) {
        return new JacksonJsonMessageConverter(jsonMapper);
    }

}
