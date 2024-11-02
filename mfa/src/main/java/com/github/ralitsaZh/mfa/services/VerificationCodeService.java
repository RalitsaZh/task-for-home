package com.github.ralitsaZh.mfa.services;

import com.github.ralitsaZh.mfa.services.model.VerificationCode;

public interface VerificationCodeService {
    VerificationCode createAndSaveVerificationCode(String email);
    void deleteExpiredCodes();
}
