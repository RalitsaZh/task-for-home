package com.github.ralitsaZh.mfa.services.model;

public class ResponseMessage {
    private String message;
    private int status;

    public ResponseMessage(String message, int status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }
}