package com.smsc.management.app.mno.controller;

import com.smsc.management.app.mno.dto.OperatorMNODTO;
import com.smsc.management.integration.BaseIntegrationTest;
import com.smsc.management.utils.ApiResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OperatorMnoControllerTest extends BaseIntegrationTest {
    @Autowired
    private OperatorMnoController operatorMnoController;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    void tearDown() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "operator_mno");
    }

    @Test
    void listProvider() {
        ResponseEntity<ApiResponse> response = operatorMnoController.listProvider();
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(ApiResponse.class, response.getBody());
        assertInstanceOf(List.class, response.getBody().data());
        List<?> result = (List<?>) response.getBody().data();
        assertEquals(0, result.size());
    }

    @Test
    void createProvider() {
        OperatorMNODTO operatorMNODTO = new OperatorMNODTO();
        operatorMNODTO.setName("Test");
        operatorMNODTO.setTlvMessageReceiptId(true);
        operatorMNODTO.setTlvMessageReceiptId(true);

        ResponseEntity<ApiResponse> response = operatorMnoController.createProvider(operatorMNODTO);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(ApiResponse.class, response.getBody());
        assertInstanceOf(OperatorMNODTO.class, response.getBody().data());
        OperatorMNODTO result = (OperatorMNODTO) response.getBody().data();
        assertEquals("Test", result.getName());
        assertTrue(result.isTlvMessageReceiptId());
    }

    @Test
    void deleteProvider() {
        OperatorMNODTO operatorMNODTO = new OperatorMNODTO();
        operatorMNODTO.setName("Test2");
        operatorMNODTO.setTlvMessageReceiptId(true);
        operatorMNODTO.setTlvMessageReceiptId(true);

        ResponseEntity<ApiResponse> responseCreate = operatorMnoController.createProvider(operatorMNODTO);
        assertNotNull(responseCreate);
        assertEquals(HttpStatus.OK, responseCreate.getStatusCode());
        assertInstanceOf(ApiResponse.class, responseCreate.getBody());
        assertInstanceOf(OperatorMNODTO.class, responseCreate.getBody().data());
        OperatorMNODTO resultCreate = (OperatorMNODTO) responseCreate.getBody().data();

        ResponseEntity<ApiResponse> response = operatorMnoController.deleteProvider(resultCreate.getId());
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void updateProvider() {
        OperatorMNODTO operatorMNODTO = new OperatorMNODTO();
        operatorMNODTO.setName("Test");
        operatorMNODTO.setTlvMessageReceiptId(true);
        operatorMNODTO.setTlvMessageReceiptId(true);

        ResponseEntity<ApiResponse> response = operatorMnoController.createProvider(operatorMNODTO);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(ApiResponse.class, response.getBody());
        assertInstanceOf(OperatorMNODTO.class, response.getBody().data());
        OperatorMNODTO result = (OperatorMNODTO) response.getBody().data();

        OperatorMNODTO updateOperatorMNODTO = new OperatorMNODTO();
        updateOperatorMNODTO.setName("Test2");
        updateOperatorMNODTO.setTlvMessageReceiptId(false);
        updateOperatorMNODTO.setTlvMessageReceiptId(false);

        ResponseEntity<ApiResponse> responseUpdate = operatorMnoController.updateProvider(updateOperatorMNODTO, result.getId());
        assertNotNull(responseUpdate);
        assertEquals(HttpStatus.OK, responseUpdate.getStatusCode());
        assertInstanceOf(ApiResponse.class, responseUpdate.getBody());
        assertInstanceOf(OperatorMNODTO.class, responseUpdate.getBody().data());
        OperatorMNODTO resultUpdate = (OperatorMNODTO) responseUpdate.getBody().data();
        assertEquals("Test2", resultUpdate.getName());
    }

    @Test
    void dataIntegrityViolationException() {
        OperatorMNODTO operatorMNODTO = new OperatorMNODTO();
        operatorMNODTO.setName("Test");
        operatorMNODTO.setTlvMessageReceiptId(true);
        operatorMNODTO.setTlvMessageReceiptId(true);

        ResponseEntity<ApiResponse> response = operatorMnoController.createProvider(operatorMNODTO);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(ApiResponse.class, response.getBody());
        assertInstanceOf(OperatorMNODTO.class, response.getBody().data());
    }
}