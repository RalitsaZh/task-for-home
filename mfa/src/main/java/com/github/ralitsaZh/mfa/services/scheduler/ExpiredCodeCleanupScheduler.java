package com.github.ralitsaZh.mfa.services.scheduler;

import com.github.ralitsaZh.mfa.services.VerificationCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ExpiredCodeCleanupScheduler {
    private final VerificationCodeService verificationCodeService;

    @Autowired
    public ExpiredCodeCleanupScheduler(VerificationCodeService verificationCodeService) {
        this.verificationCodeService = verificationCodeService;
    }

    @Scheduled(cron = "0 0/30 * * * *")
    public void scheduledCleanup() {
        verificationCodeService.deleteExpiredCodes();
    }
}
