package com.github.ralitsaZh.mfa;

import com.github.ralitsaZh.mfa.controller.MFAController;
import com.github.ralitsaZh.mfa.services.MFAService;
import com.github.ralitsaZh.mfa.services.RateLimiterService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MFAController.class)
class MfaControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RateLimiterService rateLimiterService;

    @MockBean
    private MFAService mfaService;

    private static final String TEST_EMAIL = "ralitsaTest@nexo.com";
    private static final String CODE = "fc1b9a";

    @Test
    public void testSendMfaEmailSuccess() throws Exception {
        when(rateLimiterService.isSendRateLimited(TEST_EMAIL)).thenReturn(false);
        doNothing().when(mfaService).sendMfaEmail(TEST_EMAIL);
        mockMvc.perform(post("/api/mfa/send")
                        .param("email", TEST_EMAIL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Email sent successfully to " + TEST_EMAIL))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()));
    }

    @Test
    public void testSendMfaEmailRateLimited() throws Exception {
        when(rateLimiterService.isSendRateLimited(TEST_EMAIL)).thenReturn(true);
        mockMvc.perform(post("/api/mfa/send")
                        .param("email", TEST_EMAIL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.message").value("Too many email requests. Please try again later."))
                .andExpect(jsonPath("$.status").value(HttpStatus.TOO_MANY_REQUESTS.value()));
    }

    @Test
    public void testSendMfaEmailErrorSendingEmail() throws Exception {
        when(rateLimiterService.isSendRateLimited(TEST_EMAIL)).thenReturn(false);
        doThrow(new IOException("Email server is down")).when(mfaService).sendMfaEmail(TEST_EMAIL);
        mockMvc.perform(post("/api/mfa/send")
                        .param("email", TEST_EMAIL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Error sending email: Email server is down"))
                .andExpect(jsonPath("$.status").value(HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }


    @Test
    public void testVerifyMfaCodeSuccess() throws Exception {
        when(rateLimiterService.isVerifyRateLimited(TEST_EMAIL)).thenReturn(false);
        when(mfaService.verifyMfaCode(TEST_EMAIL, CODE)).thenReturn(true);
        mockMvc.perform(get("/api/mfa/verify")
                        .param("email", TEST_EMAIL)
                        .param("code", CODE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("MFA code verified successfully."))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()));
    }

    @Test
    public void testVerifyMfaCodeRateLimited() throws Exception {
        when(rateLimiterService.isVerifyRateLimited(TEST_EMAIL)).thenReturn(true);
        mockMvc.perform(get("/api/mfa/verify")
                        .param("email", TEST_EMAIL)
                        .param("code", CODE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.message").value("Too many verification attempts."))
                .andExpect(jsonPath("$.status").value(HttpStatus.TOO_MANY_REQUESTS.value()));
    }

    @Test
    public void testVerifyMfaCodeInvalidCode() throws Exception {
        when(rateLimiterService.isVerifyRateLimited(TEST_EMAIL)).thenReturn(false);
        when(mfaService.verifyMfaCode(TEST_EMAIL, CODE)).thenReturn(false);
        mockMvc.perform(get("/api/mfa/verify")
                        .param("email", TEST_EMAIL)
                        .param("code", CODE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid MFA code."))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    public void testVerifyMfaCodeInternalServerError() throws Exception {
        when(rateLimiterService.isVerifyRateLimited(TEST_EMAIL)).thenReturn(false);
        when(mfaService.verifyMfaCode(TEST_EMAIL, CODE)).thenThrow(new RuntimeException("Service unavailable"));
        mockMvc.perform(get("/api/mfa/verify")
                        .param("email", TEST_EMAIL)
                        .param("code", CODE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Error verifying MFA code: Service unavailable"))
                .andExpect(jsonPath("$.status").value(HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }
}
