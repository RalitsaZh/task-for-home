package com.github.ralitsaZh.mfa.services.model;

public class VerificationResponse {
    private String message;
    private int status;

    public VerificationResponse(String message, int status) {
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