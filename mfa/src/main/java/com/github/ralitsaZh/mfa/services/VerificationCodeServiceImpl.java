package com.github.ralitsaZh.mfa.services;

import com.github.ralitsaZh.mfa.repository.VerificationCodeRepository;
import com.github.ralitsaZh.mfa.services.model.CodeGenerationException;
import com.github.ralitsaZh.mfa.services.model.VerificationCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.github.ralitsaZh.mfa.services.constants.Constants.*;

@Service
public class VerificationCodeServiceImpl implements VerificationCodeService {

    private static final Logger logger = LoggerFactory.getLogger(VerificationCodeServiceImpl.class);
    private final VerificationCodeRepository codeRepository;

    @Autowired
    public VerificationCodeServiceImpl(VerificationCodeRepository codeRepository) {
        this.codeRepository = codeRepository;
    }

    public VerificationCode createAndSaveVerificationCode(String email) {
        VerificationCode activeCodeForUser = codeRepository.findByEmailAndExpirationTimeIsAfterAndIsCodeUsedIsFalse(email, LocalDateTime.now());
        if (activeCodeForUser != null) {
            logger.info("Email already in use, skipping verification code");
            return activeCodeForUser;
        }
        String code = generateUniqueVerificationCode();
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setEmail(email);
        verificationCode.setVerificationCode(code);
        verificationCode.setExpirationTime(LocalDateTime.now().plusMinutes(EXPIRY_DURATION_TEN_MINUTES));
        verificationCode.setCodeUsed(false);
        codeRepository.save(verificationCode);
        logger.info("Created verification code for email: {}", email);
        return verificationCode;
    }

    public String generateUniqueVerificationCode() {
        String verificationCode;
        int attempts = INITIAL_ATTEMPTS;

        do {
            verificationCode = generateVerificationCode();
            attempts++;
        } while (codeRepository.existsByVerificationCode(verificationCode) && attempts < MAX_ATTEMPTS);

        if (attempts == MAX_ATTEMPTS) {
            throw new CodeGenerationException("Unable to generate a unique verification code after " + MAX_ATTEMPTS + " attempts.");
        }

        return verificationCode;
    }

    private String generateVerificationCode() {
        logger.info("Generating verification code...");
        return UUID.randomUUID().toString().substring(UUID_SUBSTRING_START_INDEX, UUID_SUBSTRING_END_INDEX);
    }

    @Transactional
    public void deleteExpiredCodes() {
        var currentTime = LocalDateTime.now();
        codeRepository.deleteByExpirationTimeIsBefore(currentTime);
        logger.info("Deleted expired verification codes");
    }

}
