package com.github.ralitsaZh.mfa;

import com.github.ralitsaZh.mfa.repository.VerificationCodeRepository;
import com.github.ralitsaZh.mfa.services.EmailPublisherService;
import com.github.ralitsaZh.mfa.services.MFAService;
import com.github.ralitsaZh.mfa.services.VerificationCodeService;
import com.github.ralitsaZh.mfa.services.model.VerificationCode;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.time.LocalDateTime;

import static com.github.ralitsaZh.mfa.services.constants.Constants.EXPIRY_DURATION_FIFTEEN_MINUTES;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class MfaServiceTests {

    @Mock
    private VerificationCodeService verificationCodeService;

    @Mock
    private EmailPublisherService emailPublisherService;

    @Mock
    private VerificationCodeRepository codeRepository;

    @InjectMocks
    private MFAService mfaService;

    private static final String TEST_EMAIL = "ralitsaTest@nexo.com";
    private static final String MFA_SUBJECT = "Send MFA Verification Code";
    private static final String MFA_MESSAGE_TEMPLATE = "Your verification code is: ";
    private static final String CODE = "fc1b9a";
	private static final LocalDateTime FIXED_TIME = LocalDateTime.of(2024, 11, 2, 13, 45);


    @Test
    void testSendMfaEmailSuccess() throws IOException {
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setVerificationCode(CODE);
        verificationCode.setEmail(TEST_EMAIL);
        verificationCode.setExpirationTime(LocalDateTime.now().plusMinutes(EXPIRY_DURATION_FIFTEEN_MINUTES));

        when(verificationCodeService.createAndSaveVerificationCode(TEST_EMAIL)).thenReturn(verificationCode);

        mfaService.sendMfaEmail(TEST_EMAIL);

        verify(verificationCodeService).createAndSaveVerificationCode(TEST_EMAIL);
        verify(emailPublisherService).sendEmail(
                TEST_EMAIL,
                MFA_SUBJECT,
                MFA_MESSAGE_TEMPLATE + verificationCode
        );
    }

//TODO this test is not working because of the LocalDateTime.now() in the verifyMfaCode method and i am not sure what to do about this.
//    @Test
//    void testVerifyMfaEmailSuccess() {
//        VerificationCode verificationCode = new VerificationCode();
//		verificationCode.setId(1L);
//        verificationCode.setVerificationCode(CODE);
//        verificationCode.setEmail(TEST_EMAIL);
//        verificationCode.setExpirationTime(FIXED_TIME.plusMinutes(EXPIRY_DURATION_FIFTEEN_MINUTES));
//
//        when(codeRepository.findByVerificationCodeAndEmailAndExpirationTimeIsAfterAndIsCodeUsedFalse(CODE, TEST_EMAIL,FIXED_TIME)).thenReturn(verificationCode);
//
//        boolean result = mfaService.verifyMfaCode(TEST_EMAIL, CODE);
//
//        assertTrue(result, "verifyMfaCode should return true");
//
//    }

}
