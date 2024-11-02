package com.github.ralitsaZh.mfa.services;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailConsumerService {
    private final SendGrid sendGrid;
    @Value("${mail.endpoint}")
    private String endpoint;

    @Autowired
    public EmailConsumerService(@Value("${spring.sendgrid.api-key}") String apiKey) {
        this.sendGrid = new SendGrid(apiKey);
    }

    @KafkaListener(topics = "${topic}", groupId = "${group.id}")
    public void consumeEmailMessage(Mail mail) throws IOException {
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint(endpoint);
        request.setBody(mail.build());

        try {
            Response response = sendGrid.api(request);
            System.out.println("Status Code: " + response.getStatusCode());
            System.out.println("Response Body: " + response.getBody());
            System.out.println("Response Headers: " + response.getHeaders());
        } catch (IOException ex) {
            System.out.println("Error sending email: " + ex.getMessage());
            throw ex;
        }
    }

}

