package com.smsc.management.app.user.controller;

import com.smsc.management.app.user.dto.AuthRequestDTO;
import com.smsc.management.app.user.dto.AuthResponseDTO;
import com.smsc.management.integration.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthControllerTest extends BaseIntegrationTest {
    @Autowired
    private AuthController authController;

    @Test
    void updateServiceProviderTest() {
        AuthRequestDTO authRequestDTO = new AuthRequestDTO();
        authRequestDTO.setUserName("admin");
        authRequestDTO.setPassword("admin");
        ResponseEntity<AuthResponseDTO> response = authController.authenticate(authRequestDTO);
        assertTrue(response.getStatusCode().is2xxSuccessful());
    }
}
