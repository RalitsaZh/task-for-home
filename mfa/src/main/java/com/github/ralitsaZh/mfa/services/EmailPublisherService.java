package com.github.ralitsaZh.mfa.services;

public interface EmailPublisherService {
    void sendEmail(String toEmail, String subject, String message);
}
