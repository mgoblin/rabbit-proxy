package ru.mg.rabbitmq.proxy.services;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mg.rabbitmq.proxy.controllers.PublishError;

import java.util.Optional;

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
}
