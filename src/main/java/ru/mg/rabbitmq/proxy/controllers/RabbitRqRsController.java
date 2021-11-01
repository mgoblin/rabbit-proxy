package ru.mg.rabbitmq.proxy.controllers;

import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mg.rabbitmq.proxy.services.RabbitService;
import ru.mg.rabbitmq.proxy.services.RpcException;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

@RestController
@RequestMapping("/service")
public class RabbitRqRsController {

    @Autowired
    private RabbitService rabbitService;

    @PostMapping(
            value = "/{queue}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> rpc(@PathVariable("queue") String queue, @RequestBody String messageBody) throws RpcException  {
        Message replyMessage = rabbitService.rpc(queue, messageBody);
        String reply = replyMessage == null ? null : new String(replyMessage.getBody(), StandardCharsets.UTF_8);
        return ResponseEntity.of(Optional.ofNullable(reply));
    }
}
