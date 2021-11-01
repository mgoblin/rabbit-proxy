package ru.mg.rabbitmq.proxy.controllers;

import com.rabbitmq.client.ShutdownSignalException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import ru.mg.rabbitmq.proxy.services.RpcException;


@ControllerAdvice
public class RabbitAdvice {
    @ExceptionHandler({ShutdownSignalException.class})
    public ResponseEntity<?> handleShutdownSignalException(ShutdownSignalException ex, WebRequest rq) {
        PublishError publishError = new PublishError(ex.getReason().protocolMethodId(), ex.getMessage());
        return ResponseEntity.badRequest().body(publishError);
    }

    @ExceptionHandler({RpcException.class})
    public ResponseEntity<?> handleRpcException(RpcException ex, WebRequest rq) {
        PublishError publishError = new PublishError(ex.getReason().getReplyCode(), ex.getReason().getReplyText());
        return ResponseEntity.badRequest().body(publishError);
    }
}
