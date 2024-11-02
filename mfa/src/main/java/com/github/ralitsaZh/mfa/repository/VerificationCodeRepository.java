package com.github.ralitsaZh.mfa.repository;


import com.github.ralitsaZh.mfa.services.model.VerificationCode;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface VerificationCodeRepository extends CrudRepository<VerificationCode, Long> {


    VerificationCode findByVerificationCodeAndEmailAndExpirationTimeIsAfterAndIsCodeUsedFalse(String verificationCode, String email, LocalDateTime currentTime);

    boolean existsByVerificationCode(String verificationCode);

    VerificationCode findByEmailAndExpirationTimeIsAfter(String email, LocalDateTime currentTime);

    @Modifying
    void deleteByExpirationTimeIsBefore(LocalDateTime currentTime);
}
