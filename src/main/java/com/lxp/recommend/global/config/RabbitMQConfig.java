package com.lxp.recommend.global.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String USER_EXCHANGE = "user.events";
    public static final String COURSE_EXCHANGE = "course.events";
    public static final String ENROLL_EXCHANGE = "enroll.events";

    public static final String USER_QUEUE = "recommend.user.events";
    public static final String COURSE_QUEUE = "recommend.course.events";
    public static final String ENROLL_QUEUE = "recommend.enroll.events";

    public static final String RECOMMEND_DLQ = "recommend.events.dlq";

    public static final String USER_ROUTING_KEY = "user.*";
    public static final String COURSE_ROUTING_KEY = "course.*";
    public static final String ENROLL_ROUTING_KEY = "enroll.*";

    // ============ DLQ ============
    @Bean
    public Queue recommendDlq() {
        return new Queue(RECOMMEND_DLQ, true);
    }

    // ============ Exchange ============
    @Bean
    public TopicExchange userExchange() {
        return new TopicExchange(USER_EXCHANGE);
    }

    @Bean
    public TopicExchange courseExchange() {
        return new TopicExchange(COURSE_EXCHANGE);
    }

    @Bean
    public TopicExchange enrollExchange() {
        return new TopicExchange(ENROLL_EXCHANGE);
    }

    // ============ Queue (DLQ 연결) ============
    @Bean
    public Queue userQueue() {
        return QueueBuilder.durable(USER_QUEUE)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", RECOMMEND_DLQ)
                .build();
    }

    @Bean
    public Queue courseQueue() {
        return QueueBuilder.durable(COURSE_QUEUE)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", RECOMMEND_DLQ)
                .build();
    }

    @Bean
    public Queue enrollQueue() {
        return QueueBuilder.durable(ENROLL_QUEUE)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", RECOMMEND_DLQ)
                .build();
    }

    // ============ Binding ============
    @Bean
    public Binding userBinding(Queue userQueue, TopicExchange userExchange) {
        return BindingBuilder.bind(userQueue).to(userExchange).with(USER_ROUTING_KEY);
    }

    @Bean
    public Binding courseBinding(Queue courseQueue, TopicExchange courseExchange) {
        return BindingBuilder.bind(courseQueue).to(courseExchange).with(COURSE_ROUTING_KEY);
    }

    @Bean
    public Binding enrollBinding(Queue enrollQueue, TopicExchange enrollExchange) {
        return BindingBuilder.bind(enrollQueue).to(enrollExchange).with(ENROLL_ROUTING_KEY);
    }

    // ============ Converter & Template ============
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}