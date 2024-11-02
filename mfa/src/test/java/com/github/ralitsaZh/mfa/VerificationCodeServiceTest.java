package com.github.ralitsaZh.mfa;

import com.github.ralitsaZh.mfa.repository.VerificationCodeRepository;
import com.github.ralitsaZh.mfa.services.VerificationCodeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VerificationCodeServiceTest {

    @Mock
    private VerificationCodeRepository verificationCodeRepository;

    @InjectMocks
	private VerificationCodeService verificationCodeService;

    @InjectMocks
    private VerificationCodeRepository verificationCodeRepositoryInject;

    @Test
    public void testSendMfaEmail() throws IOException {
        String email = "ralitsaTest@nexo.com";
        verificationCodeService.createAndSaveVerificationCode(email);
        verify(verificationCodeRepository, times(1)).save(any());
    }

}
