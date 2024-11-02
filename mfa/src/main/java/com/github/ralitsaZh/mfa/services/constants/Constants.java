package com.github.ralitsaZh.mfa.services.constants;

import org.springframework.http.HttpStatus;

public class Constants {

    public static final HttpStatus STATUS_OK  = HttpStatus.OK;
    public static final HttpStatus BAD_REQUEST  = HttpStatus.BAD_REQUEST;
    public static final HttpStatus TOO_MANY_REQUESTS  = HttpStatus.TOO_MANY_REQUESTS;
    public static final HttpStatus INTERNAL_SERVER_ERROR  = HttpStatus.INTERNAL_SERVER_ERROR;

    public static final int INITIAL_ATTEMPTS = 0;
    public static final int MAX_ATTEMPTS = 11;
    public static final int EXPIRY_DURATION_TEN_MINUTES = 10;
    public static final int UUID_SUBSTRING_START_INDEX = 0;
    public static final int UUID_SUBSTRING_END_INDEX = 6;

    public static final String MFA_SUBJECT = "Send MFA Verification Code";
    public static final String MFA_MESSAGE_TEMPLATE = "Your verification code is: ";

    public static final String TABLE_NAME = "mfa_codes";

    public static final int TIME_WINDOW = 10;
}