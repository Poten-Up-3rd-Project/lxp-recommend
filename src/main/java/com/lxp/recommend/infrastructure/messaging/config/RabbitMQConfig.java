    package com.lxp.recommend.infrastructure.messaging.config;

    import org.springframework.amqp.core.*;
    import org.springframework.amqp.rabbit.core.RabbitAdmin;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.amqp.rabbit.connection.ConnectionFactory;

    @Configuration
    public class RabbitMQConfig {

        public static final String COURSE_EXCHANGE = "course.exchange";
        public static final String COURSE_EVENTS_QUEUE = "course.events";
        public static final String COURSE_ROUTING_KEY = "course.#";

        public static final String USER_EXCHANGE = "user.events";
        public static final String USER_EVENTS_QUEUE = "user.events.queue";
        public static final String USER_ROUTING_KEY = "user.#";

        @Bean
        public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
            return new RabbitAdmin(connectionFactory);
        }

        @Bean
        public TopicExchange courseExchange() {
            return new TopicExchange(COURSE_EXCHANGE);
        }

        @Bean
        public Queue courseEventsQueue() {
            return QueueBuilder.durable(COURSE_EVENTS_QUEUE).build();
        }

        @Bean
        public Binding courseBinding(Queue courseEventsQueue, TopicExchange courseExchange) {
            return BindingBuilder
                    .bind(courseEventsQueue)
                    .to(courseExchange)
                    .with(COURSE_ROUTING_KEY);
        }

        @Bean
        public TopicExchange userExchange() {
            return new TopicExchange(USER_EXCHANGE);
        }

        @Bean
        public Queue userEventsQueue() {
            return QueueBuilder.durable(USER_EVENTS_QUEUE).build();
        }

        @Bean
        public Binding userBinding(Queue userEventsQueue, TopicExchange userExchange) {
            return BindingBuilder
                    .bind(userEventsQueue)
                    .to(userExchange)
                    .with(USER_ROUTING_KEY);
        }

    }