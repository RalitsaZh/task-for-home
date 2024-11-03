package com.github.ralitsaZh.mfa;

import com.github.ralitsaZh.mfa.services.EmailConsumerService;
import com.github.ralitsaZh.mfa.services.EmailPublisherServiceImpl;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.*;

@SpringBootTest
class EmailServiceTest {


	@MockBean
	private KafkaTemplate<String, Mail> kafkaTemplate;

	@Autowired
	private EmailPublisherServiceImpl emailPublisherService;

	@Mock
	private EmailConsumerService emailConsumerService;

	@Mock
	private SendGrid sendGrid;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	private static final String FROM_ADDRESS = "ralicazz@uni-sofia.bg";
	private static final String TOPIC = "mfa_notification";
	private static final String TO_EMAIL = "ralitsaTest@nexo.com";
	private static final String SUBJECT  = "Test Subject";
	private static final String MESSAGE = "This is a test message.";

	@Test
	void testSendEmail() {
		Email from = new Email(FROM_ADDRESS);
		Email to = new Email(TO_EMAIL);
		Content content = new Content("text/plain", MESSAGE);
		Mail expectedMail = new Mail(from, SUBJECT, to, content);

		emailPublisherService.sendEmail(TO_EMAIL, SUBJECT, MESSAGE);

		ArgumentCaptor<Mail> mailCaptor = ArgumentCaptor.forClass(Mail.class);
		verify(kafkaTemplate).send(eq(TOPIC), mailCaptor.capture());

		Mail actualMail = mailCaptor.getValue();
		assertNotNull(actualMail, "Mail object should not be null");

		assertEquals(expectedMail.getFrom().getEmail(), actualMail.getFrom().getEmail(), FROM_ADDRESS);
		assertEquals(expectedMail.getSubject(), actualMail.getSubject(), SUBJECT);
		assertEquals(expectedMail.getPersonalization().get(0).getTos().get(0).getEmail(),
		actualMail.getPersonalization().get(0).getTos().get(0).getEmail(), TO_EMAIL);
		assertEquals(expectedMail.getContent().get(0).getValue(), actualMail.getContent().get(0).getValue(), MESSAGE);

	}

}