package com.taskflow.userservice.infrastructure.messaging;

import com.taskflow.userservice.domain.event.UserCreatedEvent;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Componente temporário para testar a PUBLICAÇÃO de eventos.
 *
 * Ele simula o comportamento de um Serviço Consumidor (como o TaskManagementService)
 * configurando sua própria fila e Binding para a Exchange do usuário.
 *
 * Após o teste de sucesso, esta classe deve ser REMOVIDA do UserService.
 */
@Component
public class TestEventListener {

    @Value("${rabbitmq.exchange.user}")
    private String userExchangeName;

    @Value("${rabbitmq.routing.user.created}")
    private String userCreatedRoutingKey;

    // Nome da Fila específica para este teste
    private static final String TEST_QUEUE_NAME = "user.created.test.queue";


    // 1. Configurar a Fila do Consumidor Temporário
    @Bean
    public Queue testQueue() {
        // A fila precisa ser durável para sobreviver a reinicializações.
        return new Queue(TEST_QUEUE_NAME, true);
    }

    // 2. Configurar o Binding entre a Exchange (Produtor) e a Fila (Consumidor)
    @Bean
    public Binding bindingUserCreated(TopicExchange userExchange, Queue testQueue) {
        // Conecta a fila ao mesmo nome de Exchange e à mesma Routing Key que o Produtor usa.
        return BindingBuilder.bind(testQueue)
                .to(userExchange)
                .with(userCreatedRoutingKey);
    }

    // 3. Listener Real: Espera por mensagens na fila
    @RabbitListener(queues = TEST_QUEUE_NAME)
    public void handleUserCreatedEvent(UserCreatedEvent event) {
        // Se esta linha for impressa, a publicação está FUNCIONANDO CORRETAMENTE.
        System.out.println("==================================================================================");
        System.out.println("✅ ✅ TESTE DE EVENTO BEM-SUCEDIDO! Mensagem recebida no Listener Temporário! ✅ ✅ ");
        System.out.println("Dados do Evento Recebido: " + event);
        System.out.println("User ID: " + event.userId() + " | Email: " + event.email());
        System.out.println("==================================================================================");
        // Em um consumidor real, aqui você teria a lógica de negócio para sincronizar dados.
    }
}