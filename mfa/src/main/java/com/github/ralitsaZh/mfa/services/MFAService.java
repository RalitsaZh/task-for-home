package com.github.ralitsaZh.mfa.services;

import com.github.ralitsaZh.mfa.repository.VerificationCodeRepository;
import com.github.ralitsaZh.mfa.services.model.VerificationCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;

import static com.github.ralitsaZh.mfa.services.constants.Constants.MFA_MESSAGE_TEMPLATE;
import static com.github.ralitsaZh.mfa.services.constants.Constants.MFA_SUBJECT;

@Service
public class MFAService {

    private static final Logger logger = LoggerFactory.getLogger(MFAService.class);

    private final VerificationCodeService verificationCodeService;
    private final EmailPublisherService emailPublisherService;
    private final VerificationCodeRepository codeRepository;

    @Autowired
    public MFAService(VerificationCodeService verificationCodeService, EmailPublisherService emailPublisherService, VerificationCodeRepository codeRepository) {
        this.verificationCodeService = verificationCodeService;
        this.emailPublisherService = emailPublisherService;
        this.codeRepository = codeRepository;
    }


    @Transactional
    public void sendMfaEmail(String email) throws IOException {
        VerificationCode verificationCode = verificationCodeService.createAndSaveVerificationCode(email);
        String message = MFA_MESSAGE_TEMPLATE + verificationCode.toString();
        logger.info("Sending email to {}", email);
        emailPublisherService.sendEmail(email, MFA_SUBJECT, message);
    }

    @Transactional
    public boolean verifyMfaCode(String email, String code) {
        var currentTime = LocalDateTime.now();
        var optionalCode = codeRepository.findByVerificationCodeAndEmailAndExpirationTimeIsAfterAndIsCodeUsedFalse(code, email, currentTime);
        if (optionalCode != null && optionalCode.getId() != null) {
            optionalCode.setCodeUsed(true);
            codeRepository.save(optionalCode);
            return true;
        }
        return false;
    }
}
