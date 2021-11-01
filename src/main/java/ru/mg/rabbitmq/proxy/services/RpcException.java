package ru.mg.rabbitmq.proxy.services;

import org.springframework.amqp.core.AmqpMessageReturnedException;

public class RpcException extends Exception {
    private AmqpMessageReturnedException reason;

    public RpcException(AmqpMessageReturnedException ex) {
        super(ex);
        this.reason = ex;
    }

    public AmqpMessageReturnedException getReason() {
        return reason;
    }
}
