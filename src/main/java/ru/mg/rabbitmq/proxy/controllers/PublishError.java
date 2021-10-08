package ru.mg.rabbitmq.proxy.controllers;

public class PublishError {
    private int errorCode;
    private String errorText;

    public PublishError(int errorCode, String errorText) {
        this.errorCode = errorCode;
        this.errorText = errorText;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorText() {
        return errorText;
    }
}
