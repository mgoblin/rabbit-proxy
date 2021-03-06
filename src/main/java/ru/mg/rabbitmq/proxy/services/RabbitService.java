package ru.mg.rabbitmq.proxy.services;

import org.springframework.amqp.core.AmqpMessageReturnedException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import ru.mg.rabbitmq.proxy.controllers.PublishError;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

@Service
public class RabbitService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public Optional<PublishError> putMessage(String queueName, Message message) {
        CorrelationData correlationData = new CorrelationData();
        rabbitTemplate.invoke(t -> {
            t.send("", queueName, message, correlationData);

            t.waitForConfirmsOrDie(10_000);
            return correlationData.getReturned() == null;

        });

        if (correlationData.getReturned() != null) {
            int replyCode = correlationData.getReturned().getReplyCode();
            String replyText = correlationData.getReturned().getReplyText();
            PublishError publishError = new PublishError(replyCode, replyText);

            return Optional.of(publishError);
        } else {
            return Optional.empty();
        }
    }

    public Message getMessage(String queueName) {
        return rabbitTemplate.receive(queueName, 1_000);
    }

    public Message rpc(String queueName, String messageBody) throws RpcException {
        try {
            Message message = MessageBuilder
                    .withBody(messageBody.getBytes(StandardCharsets.UTF_8))
                    .setContentType(MediaType.APPLICATION_JSON_VALUE)
                    .setCorrelationId(UUID.randomUUID().toString())
                    .setReplyTo("amq.rabbitmq.reply-to")
                    .build();

            return rabbitTemplate.sendAndReceive("", queueName, message);
        } catch (AmqpMessageReturnedException ex) {
            throw new RpcException(ex);
        }
    }

    @PostConstruct
    public void postConstructRabbitTemplate() {
        rabbitTemplate.setUserCorrelationId(true);
        rabbitTemplate.setUseDirectReplyToContainer(true);
    }
}
