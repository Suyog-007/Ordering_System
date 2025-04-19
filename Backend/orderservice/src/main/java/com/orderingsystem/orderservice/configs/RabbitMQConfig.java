package com.orderingsystem.orderservice.configs;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

@Configuration
@EnableRabbit
public class RabbitMQConfig {

    // Declare the exchange
    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange("order-exchange");
    }

    // Declare the queue
    @Bean
    public Queue orderQueue() {
        return new Queue("order-queue", true);
    }

    // Declare the binding between queue and exchange
    @Bean
    public Binding orderBinding() {
        return BindingBuilder.bind(orderQueue())
                .to(orderExchange())
                .with("order-routing-key");
    }

    // Create Jackson2JsonMessageConverter
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // Configure RabbitTemplate to use the Jackson message converter
    @Bean
    public RabbitTemplate rabbitTemplate(CachingConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
        return rabbitTemplate;
    }
}

