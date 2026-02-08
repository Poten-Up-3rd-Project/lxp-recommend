package com.lxp.recommend.global.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "lxp.events";

    public static final String USER_QUEUE = "recommend.user.events";
    public static final String COURSE_QUEUE = "recommend.course.events";
    public static final String ENROLL_QUEUE = "recommend.enroll.events";

    public static final String USER_ROUTING_KEY = "user.*";
    public static final String COURSE_ROUTING_KEY = "course.*";
    public static final String ENROLL_ROUTING_KEY = "enroll.*";

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue userQueue() {
        return new Queue(USER_QUEUE, true);
    }

    @Bean
    public Queue courseQueue() {
        return new Queue(COURSE_QUEUE, true);
    }

    @Bean
    public Queue enrollQueue() {
        return new Queue(ENROLL_QUEUE, true);
    }

    @Bean
    public Binding userBinding(Queue userQueue, TopicExchange exchange) {
        return BindingBuilder.bind(userQueue).to(exchange).with(USER_ROUTING_KEY);
    }

    @Bean
    public Binding courseBinding(Queue courseQueue, TopicExchange exchange) {
        return BindingBuilder.bind(courseQueue).to(exchange).with(COURSE_ROUTING_KEY);
    }

    @Bean
    public Binding enrollBinding(Queue enrollQueue, TopicExchange exchange) {
        return BindingBuilder.bind(enrollQueue).to(exchange).with(ENROLL_ROUTING_KEY);
    }

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
