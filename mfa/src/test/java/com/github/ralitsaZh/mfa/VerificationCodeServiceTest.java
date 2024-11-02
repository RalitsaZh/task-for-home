package com.github.ralitsaZh.mfa;

import com.github.ralitsaZh.mfa.repository.VerificationCodeRepository;
import com.github.ralitsaZh.mfa.services.VerificationCodeServiceImpl;
import com.github.ralitsaZh.mfa.services.model.CodeGenerationException;
import com.github.ralitsaZh.mfa.services.model.VerificationCode;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static com.github.ralitsaZh.mfa.services.constants.Constants.EXPIRY_DURATION_TEN_MINUTES;
import static com.github.ralitsaZh.mfa.services.constants.Constants.MAX_ATTEMPTS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class VerificationCodeServiceTest {

    @Mock
    private VerificationCodeRepository verificationCodeRepositoryMock;

    @InjectMocks
    private VerificationCodeServiceImpl verificationCodeService;

    private static final String TEST_EMAIL = "ralitsaTest@nexo.com";

    @Test
    public void testCreateAndSaveNewCode() {
        when(verificationCodeRepositoryMock.findByEmailAndExpirationTimeIsAfterAndIsCodeUsedIsFalse(anyString(), any(LocalDateTime.class)))
                .thenReturn(null);
        when(verificationCodeRepositoryMock.existsByVerificationCode(anyString())).thenReturn(false);

        VerificationCode generatedCode = verificationCodeService.createAndSaveVerificationCode(TEST_EMAIL);

        assertNotNull(generatedCode, "Generated verification code should not be null");
        assertEquals(TEST_EMAIL, generatedCode.getEmail(), "Email should match the input email");
        assertNotNull(generatedCode.getVerificationCode(), "Verification code should be generated");
        assertFalse(generatedCode.getCodeUsed(), "Code should not be marked as used");
        assertTrue(generatedCode.getExpirationTime().isAfter(LocalDateTime.now()), "Expiration time should be in the future");

        verify(verificationCodeRepositoryMock, times(1)).save(any());
    }

    @Test
    public void testCreateAndSaveVerificationCodeExistingActiveCode() {
        VerificationCode activeCode = new VerificationCode();
        activeCode.setEmail(TEST_EMAIL);
        activeCode.setVerificationCode("existing_code");
        activeCode.setExpirationTime(LocalDateTime.now().plusMinutes(EXPIRY_DURATION_TEN_MINUTES));
        activeCode.setCodeUsed(false);

        when(verificationCodeRepositoryMock.findByEmailAndExpirationTimeIsAfterAndIsCodeUsedIsFalse(anyString(), any(LocalDateTime.class)))
                .thenReturn(activeCode);

        VerificationCode returnedCode = verificationCodeService.createAndSaveVerificationCode(TEST_EMAIL);

        assertEquals(activeCode, returnedCode, "Returned code should be the existing active code");
        verify(verificationCodeRepositoryMock, never()).save(any(VerificationCode.class));
    }


    @Test
    public void testGenerateUniqueVerificationCodeUniqueCodeGeneration() {
        when(verificationCodeRepositoryMock.existsByVerificationCode(anyString())).thenReturn(false);

        String uniqueCode = verificationCodeService.generateUniqueVerificationCode();

        assertNotNull(uniqueCode, "Unique code should not be null");
        assertFalse(uniqueCode.isEmpty(), "Unique code should have content");
    }


    @Test
    public void testGenerateUniqueVerificationCode_ThrowsExceptionAfterMaxAttempts() {
        when(verificationCodeRepositoryMock.existsByVerificationCode(anyString())).thenReturn(true);

        CodeGenerationException exception = assertThrows(
                CodeGenerationException.class,
                () -> verificationCodeService.generateUniqueVerificationCode(),
                "Expected generateUniqueVerificationCode() to throw, but it didn't"
        );

        assertEquals(
                "Unable to generate a unique verification code after " + MAX_ATTEMPTS + " attempts.",
                exception.getMessage()
        );

        verify(verificationCodeRepositoryMock, times(MAX_ATTEMPTS)).existsByVerificationCode(anyString());
    }

}
