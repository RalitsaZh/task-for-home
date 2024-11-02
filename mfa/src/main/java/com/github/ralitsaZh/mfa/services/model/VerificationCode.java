package com.github.ralitsaZh.mfa.services.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

import static com.github.ralitsaZh.mfa.services.constants.Constants.TABLE_NAME;

@Entity
@Table(name = TABLE_NAME)
public class VerificationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mfaCodesSeq")
    @SequenceGenerator(name = "mfaCodesSeq", sequenceName = "mfa_codes_seq", allocationSize = 1)
    private Long id;
    private String email;
    private String verificationCode;
    private LocalDateTime expirationTime;
    private Boolean isCodeUsed;

    public VerificationCode() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setVerificationCode(String code) {
        this.verificationCode = code;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public LocalDateTime getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(LocalDateTime expirationTime) {
        this.expirationTime = expirationTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getCodeUsed() {
        return isCodeUsed;
    }

    public void setCodeUsed(Boolean codeUsed) {
        isCodeUsed = codeUsed;
    }

    @Override
    public String toString() {
        return verificationCode != null ? verificationCode : "";
    }

}
