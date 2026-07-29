package com.sprint.training.constants;

public class RabbitConstants {

    private RabbitConstants(){}

    public static final String EXCHANGE_GYM = "gym.events";
    public static final String QUEUE_NOTIFICATIONS = "gym.notifications.queue";
    public static final String ROUTING_KEY_ACCESS_REGISTERED = "gym.access.registered";
    public static final String BINDING_PATTERN_ACCESS = "gym.access.*";
}
