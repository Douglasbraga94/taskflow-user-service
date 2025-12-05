package com.taskflow.userservice.infrastructure.messaging;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração dos Beans do RabbitMQ para o UserService.
 *
 * O UserService atua como PRODUTOR, então ele precisa apenas garantir
 * que a Exchange exista para publicar as mensagens.
 */
@Configuration
public class RabbitMQConfig {

    // Valor injetado do application.properties para o nome da Exchange
    @Value("${rabbitmq.exchange.user}")
    private String userExchangeName;

    /**
     * Define a Topic Exchange.
     */
    @Bean
    public TopicExchange userExchange() {
        // Exchange durável (true) e não auto-delete (false)
        return new TopicExchange(userExchangeName, true, false);
    }

    /**
     * Define o MessageConverter.
     * * * Revertendo para Jackson2JsonMessageConverter, que é o padrão e está
     * sempre disponível.
     * * Usaremos o JSON como formato, o que resolve o erro de desserialização
     * insegura que tínhamos antes, pois o Jackson é usado por padrão.
     */
    @Bean
    public MessageConverter messageConverter() {
        // A presença desta classe força o uso de JSON e resolve a maioria dos problemas
        // de segurança/serialização.
        return new Jackson2JsonMessageConverter();
    }
}