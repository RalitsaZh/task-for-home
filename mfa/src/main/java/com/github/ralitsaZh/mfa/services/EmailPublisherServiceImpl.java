package com.github.ralitsaZh.mfa.services;

import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
public class EmailPublisherServiceImpl implements EmailPublisherService {

    @Value("${spring.mail.from-address}")
    private String fromAddress;
    @Value("${topic}")
    private String topic;
    private final KafkaTemplate<String, Mail> kafkaTemplate;

    private static final Logger logger = LoggerFactory.getLogger(EmailPublisherServiceImpl.class);

    @Autowired
    public EmailPublisherServiceImpl(KafkaTemplate<String, Mail> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEmail(String toEmail, String subject, String message) {
        Email from = new Email(fromAddress);
        Email to = new Email(toEmail);
        Content content = new Content("text/plain", message);
        Mail mail = new Mail(from, subject, to, content);

        kafkaTemplate.send(topic, mail);
        logger.info("Email sent to Kafka {}", toEmail);
    }

}
