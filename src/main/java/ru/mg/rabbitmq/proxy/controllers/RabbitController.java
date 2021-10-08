package ru.mg.rabbitmq.proxy.controllers;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mg.rabbitmq.proxy.services.RabbitService;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RestController
public class RabbitController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitService rabbitService;

    @GetMapping(value = "/{queue}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getMessage(@PathVariable("queue") String queue) {

        Message message = rabbitTemplate.receive(queue, 1_000);
        return message != null ?
            ResponseEntity.ok(new String(message.getBody())) :
            ResponseEntity.ok(null);
    }

    @PostMapping(
            value = "/{queue}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> putMessage(@PathVariable("queue") String queue, @RequestBody String messageBody) {

        Message message = MessageBuilder
                .withBody(messageBody.getBytes(StandardCharsets.UTF_8))
                .setContentType(MediaType.APPLICATION_JSON_VALUE)
                .build();

        Optional<PublishError> optPublishError = rabbitService.putMessage(queue, message);

        return  optPublishError.isEmpty() ?
            ResponseEntity.ok("") : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(optPublishError.get());

    }
}
