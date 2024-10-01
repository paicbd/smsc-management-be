package com.smsc.management.app.gateway.controller;

import com.smsc.management.app.gateway.dto.GatewaysDTO;
import com.smsc.management.app.gateway.dto.ParseGatewaysDTO;
import com.smsc.management.app.mno.model.entity.OperatorMno;
import com.smsc.management.app.mno.model.repository.OperatorMnoRepository;
import com.smsc.management.integration.BaseIntegrationTest;
import com.smsc.management.utils.ApiResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;


class GatewaysControllerTest extends BaseIntegrationTest {

    @Autowired
    private GatewaysController gatewaysController;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private OperatorMnoRepository operatorMnoRepository;

    @AfterEach
    void tearDown() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "gateways");
    }

    @Test
    void listGateway() {
        ResponseEntity<ApiResponse> response = gatewaysController.listGateway();
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(ApiResponse.class, response.getBody());
        assertInstanceOf(List.class, response.getBody().data());
        List<?> result = (List<?>) response.getBody().data();
        assertEquals(0, result.size());
    }

    @Test
    void createGateway() {

        OperatorMno operatorMno = new OperatorMno();
        operatorMno.setName("NewMNO");
        operatorMnoRepository.save(operatorMno);

        GatewaysDTO gatewaysDTO = new GatewaysDTO();
        gatewaysDTO.setNetworkId(1);
        gatewaysDTO.setName("This is a Test Gateway");
        gatewaysDTO.setSystemId("GW_TEST");
        gatewaysDTO.setPassword("GwPwd58");
        gatewaysDTO.setIp("127.0.0.1");
        gatewaysDTO.setPort(2776);
        gatewaysDTO.setBindType("TRANSCEIVER");
        gatewaysDTO.setSystemType("SystemType");
        gatewaysDTO.setInterfaceVersion("IF_34");
        gatewaysDTO.setSessionsNumber(4);
        gatewaysDTO.setAddressTon(2);
        gatewaysDTO.setAddressNpi(1);
        gatewaysDTO.setAddressRange("");
        gatewaysDTO.setTps(50);
        gatewaysDTO.setEnabled(1);
        gatewaysDTO.setEnquireLinkPeriod(5000);
        gatewaysDTO.setRequestDlr(Boolean.TRUE);
        gatewaysDTO.setNoRetryErrorCode("Retry");
        gatewaysDTO.setRetryAlternateDestinationErrorCode("RetryAlternate");
        gatewaysDTO.setBindTimeout(500);
        gatewaysDTO.setBindRetryPeriod(5000);
        gatewaysDTO.setPduTimeout(5000);
        gatewaysDTO.setPduDegree(500);
        gatewaysDTO.setThreadPoolSize(150);
        gatewaysDTO.setMnoId(operatorMno.getId());
        gatewaysDTO.setProtocol("SMPP");
        gatewaysDTO.setAutoRetryErrorCode("AutoRetry");
        gatewaysDTO.setEncodingIso88591(3);
        gatewaysDTO.setEncodingGsm7(0);
        gatewaysDTO.setEncodingUcs2(2);
        gatewaysDTO.setSplitMessage(Boolean.FALSE);
        gatewaysDTO.setSplitSmppType("TLV");
        ResponseEntity<ApiResponse> response = gatewaysController.create(gatewaysDTO);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(ApiResponse.class, response.getBody());
        assertInstanceOf(ParseGatewaysDTO.class, response.getBody().data());
        ParseGatewaysDTO result = (ParseGatewaysDTO) response.getBody().data();
        assertEquals("This is a Test Gateway", result.getName());
        assertTrue(result.isRequestDlr());
        Assertions.assertFalse(result.isSplitMessage());
        assertEquals(5000, result.getBindRetryPeriod());
        assertEquals("TRANSCEIVER", result.getBindType());
    }

    @Test
    void updateGateway() {
        OperatorMno operatorMno = new OperatorMno();
        operatorMno.setName("NewMNO2");
        operatorMnoRepository.save(operatorMno);

        GatewaysDTO gatewaysDTO = new GatewaysDTO();
        gatewaysDTO.setNetworkId(1);
        gatewaysDTO.setName("This is a Test Gateway");
        gatewaysDTO.setSystemId("GW_TEST");
        gatewaysDTO.setPassword("GwPwd58");
        gatewaysDTO.setIp("127.0.0.1");
        gatewaysDTO.setPort(2776);
        gatewaysDTO.setBindType("TRANSCEIVER");
        gatewaysDTO.setSystemType("SystemType");
        gatewaysDTO.setInterfaceVersion("IF_34");
        gatewaysDTO.setSessionsNumber(4);
        gatewaysDTO.setAddressTon(2);
        gatewaysDTO.setAddressNpi(1);
        gatewaysDTO.setAddressRange("");
        gatewaysDTO.setTps(50);
        gatewaysDTO.setEnabled(1);
        gatewaysDTO.setEnquireLinkPeriod(5000);
        gatewaysDTO.setRequestDlr(Boolean.TRUE);
        gatewaysDTO.setNoRetryErrorCode("Retry");
        gatewaysDTO.setRetryAlternateDestinationErrorCode("RetryAlternate");
        gatewaysDTO.setBindTimeout(500);
        gatewaysDTO.setBindRetryPeriod(5000);
        gatewaysDTO.setPduTimeout(5000);
        gatewaysDTO.setPduDegree(500);
        gatewaysDTO.setThreadPoolSize(150);
        gatewaysDTO.setMnoId(operatorMno.getId());
        gatewaysDTO.setProtocol("SMPP");
        gatewaysDTO.setAutoRetryErrorCode("AutoRetry");
        gatewaysDTO.setEncodingIso88591(3);
        gatewaysDTO.setEncodingGsm7(0);
        gatewaysDTO.setEncodingUcs2(2);
        gatewaysDTO.setSplitMessage(Boolean.FALSE);
        gatewaysDTO.setSplitSmppType("TLV");
        ResponseEntity<ApiResponse> response = gatewaysController.create(gatewaysDTO);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(ApiResponse.class, response.getBody());
        assertInstanceOf(ParseGatewaysDTO.class, response.getBody().data());
        ParseGatewaysDTO result = (ParseGatewaysDTO) response.getBody().data();

        GatewaysDTO updatedGateway = gatewaysDTO;
        updatedGateway.setName("This is the updated Test Gateway");
        updatedGateway.setProtocol("HTTP");
        updatedGateway.setSplitSmppType("UDH");
        updatedGateway.setThreadPoolSize(200);

        ResponseEntity<ApiResponse> updateResponse = gatewaysController.update(updatedGateway, result.getNetworkId());
        assertNotNull(updateResponse);
        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        assertInstanceOf(ApiResponse.class, updateResponse.getBody());
        assertInstanceOf(ParseGatewaysDTO.class, updateResponse.getBody().data());
        ParseGatewaysDTO updateResult = (ParseGatewaysDTO) updateResponse.getBody().data();

        assertEquals("This is the updated Test Gateway", updateResult.getName());
        assertEquals("UDH", updateResult.getSplitSmppType());
        assertEquals("HTTP", updateResult.getProtocol());
    }

}