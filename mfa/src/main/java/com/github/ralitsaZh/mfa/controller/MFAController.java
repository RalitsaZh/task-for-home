package com.github.ralitsaZh.mfa.controller;

import com.github.ralitsaZh.mfa.services.MFAService;
import com.github.ralitsaZh.mfa.services.RateLimiterService;
import com.github.ralitsaZh.mfa.services.model.ResponseMessage;
import com.github.ralitsaZh.mfa.services.model.VerificationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;

import static com.github.ralitsaZh.mfa.services.constants.Constants.*;

@RestController
@RequestMapping("/api/mfa")
public class MFAController {

    private final MFAService mfaService;
    private final RateLimiterService rateLimiterService;

    @Autowired
    public MFAController(MFAService mfaService, RateLimiterService rateLimiterService) {
        this.mfaService = mfaService;
        this.rateLimiterService = rateLimiterService;
    }

    @PostMapping("/send")
    public ResponseEntity<ResponseMessage> sendMfaEmail(@RequestParam String email) {
        if (rateLimiterService.isSendRateLimited(email)) {
            ResponseMessage response = new ResponseMessage("Too many email requests. Please try again later.", TOO_MANY_REQUESTS.value());
            return ResponseEntity.status(TOO_MANY_REQUESTS).body(response);
        }
        try {
            mfaService.sendMfaEmail(email);
            ResponseMessage response = new ResponseMessage("Email sent successfully to " + email, STATUS_OK.value());
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            ResponseMessage response = new ResponseMessage("Error sending email: " + e.getMessage(), INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<VerificationResponse> verifyMfaCode(@RequestParam String email, @RequestParam String code) {
        if (rateLimiterService.isVerifyRateLimited(email)) {
            VerificationResponse response = new VerificationResponse("Too many verification attempts.", TOO_MANY_REQUESTS.value());
            return ResponseEntity.status(TOO_MANY_REQUESTS).body(response);
        }
        try {
            boolean isValid = mfaService.verifyMfaCode(email, code);
            if (isValid) {
                VerificationResponse response = new VerificationResponse("MFA code verified successfully.", STATUS_OK.value());
                return ResponseEntity.ok(response);
            } else {
                VerificationResponse response = new VerificationResponse("Invalid MFA code.", BAD_REQUEST.value());
                return ResponseEntity.status(BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            VerificationResponse response = new VerificationResponse(
                    "Error verifying MFA code: " + e.getMessage(), INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
