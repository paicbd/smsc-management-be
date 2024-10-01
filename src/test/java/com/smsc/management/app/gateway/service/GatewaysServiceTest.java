package com.smsc.management.app.gateway.service;

import com.smsc.management.app.gateway.dto.GatewaysDTO;
import com.smsc.management.app.gateway.dto.ParseGatewaysDTO;
import com.smsc.management.app.gateway.mapper.GatewaysMapper;
import com.smsc.management.app.gateway.model.entity.Gateways;
import com.smsc.management.app.gateway.model.repository.GatewaysRepository;
import com.smsc.management.app.mno.model.entity.OperatorMno;
import com.smsc.management.app.mno.model.repository.OperatorMnoRepository;
import com.smsc.management.app.sequence.SequenceNetworksIdGenerator;
import com.smsc.management.utils.ApiResponse;
import com.smsc.management.utils.AppProperties;
import com.smsc.management.utils.Constants;
import com.smsc.management.utils.UtilsBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

import static com.smsc.management.utils.Constants.DELETED_STATUS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
class GatewaysServiceTest {

    @Mock
    GatewaysRepository gatewaysRepo;

    @Mock
    GatewaysMapper gatewaysMapper;

    @Mock
    UtilsBase utilsBase;

    @Mock
    OperatorMnoRepository operatorRepo;

    @Mock
    SequenceNetworksIdGenerator seqGateway;

    @Mock
    AppProperties appProperties;

    @InjectMocks
    GatewaysService gatewaysService;

    @Test
    void createAnyExceptionTest() {
        when(gatewaysRepo.findAllBySystemIdAndEnabledNot(anyString(), anyInt())).thenReturn(null);
        when(gatewaysRepo.save(any())).thenThrow(new RuntimeException("RuntimeException"));
        ApiResponse response = gatewaysService.create(new GatewaysDTO());
        assertNotNull(response);
        assertEquals(500, response.status());
    }

    @Test
    void getGatewaysExceptionTest() {
        when(gatewaysRepo.findByEnabledNot(anyInt())).thenThrow(new RuntimeException("RuntimeException"));
        ApiResponse response = gatewaysService.getGateways();
        assertNotNull(response);
        assertEquals(500, response.status());
    }

    @Test
    void createAlreadyReferencedGatewayTest() {
        when(gatewaysRepo.findAllBySystemIdAndEnabledNot(anyString(), anyInt())).thenReturn(List.of(new Gateways()));
        GatewaysDTO gatewaysDTO = new GatewaysDTO();
        gatewaysDTO.setSystemId("SystemID");
        ApiResponse response = gatewaysService.create(gatewaysDTO);
        assertNotNull(response);
        assertEquals(400, response.status());
    }

    @Test
    void createDataIntegrityViolationExceptionTest() {
        when(gatewaysRepo.findAllBySystemIdAndEnabledNot(anyString(), anyInt())).thenReturn(List.of(new Gateways()));
        when(gatewaysMapper.toEntity(any())).thenReturn(new Gateways());
        when(gatewaysRepo.save(any())).thenThrow(new DataIntegrityViolationException("DataIntegrityViolationException"));
        GatewaysDTO gatewaysDTO = new GatewaysDTO();
        assertThrows(DataIntegrityViolationException.class, () -> gatewaysService.create(gatewaysDTO));
    }

    @Test
    void findSystemIdAndEnableDefaultCaseTest() {
        assertFalse(gatewaysService.existsSystemIdAndEnabled("Systemid", -1, -1, "AnyString"));
    }

    @Test
    void updateAlreadyDeletedGatewayTest() {
        Gateways testGateway = new Gateways();
        testGateway.setEnabled(Constants.DELETED_STATUS);
        when(gatewaysRepo.findById(anyInt())).thenReturn(testGateway);
        GatewaysDTO gatewaysDTO = new GatewaysDTO();
        gatewaysDTO.setPassword("passW00rD");
        gatewaysDTO.setSystemId("SystemID");

        ApiResponse response = gatewaysService.update(-1, gatewaysDTO);
        assertNotNull(response);
        assertEquals(400, response.status());
    }

    @Test
    void updateNotFoundGatewayTest() {
        when(gatewaysRepo.findById(anyInt())).thenReturn(null);
        GatewaysDTO gatewaysDTO = new GatewaysDTO();
        gatewaysDTO.setPassword("passW00rD");
        gatewaysDTO.setSystemId("SystemID");
        ApiResponse response = gatewaysService.update(-1, gatewaysDTO);
        assertNotNull(response);
        assertEquals(404, response.status());
    }

    @Test
    void updateAlreadyReferencedGatewayTest() {
        when(gatewaysRepo.findBySystemIdAndEnabledNotAndNetworkIdNot(anyString(), anyInt(), anyInt())).thenReturn(List.of(new Gateways()));
        GatewaysDTO gatewaysDTO = new GatewaysDTO();
        gatewaysDTO.setPassword("passW00rD");
        gatewaysDTO.setSystemId("SystemID");
        ApiResponse response = gatewaysService.update(-1, gatewaysDTO);
        assertNotNull(response);
        assertEquals(400, response.status());
    }

    @Test
    void updateDataIntegrityViolationExceptionTest() {
        when(gatewaysRepo.findById(anyInt())).thenReturn(new Gateways());
        when(gatewaysRepo.save(any())).thenThrow(new DataIntegrityViolationException("DataIntegrityViolationException"));
        GatewaysDTO gatewaysDTO = new GatewaysDTO();
        assertThrows(DataIntegrityViolationException.class, () -> gatewaysService.update(-1, gatewaysDTO));
    }

    @Test
    void updateGatewayAnyExceptionTest() {
        when(gatewaysRepo.findBySystemIdAndEnabledNotAndNetworkIdNot(anyString(), anyInt(), anyInt())).thenReturn(null);
        when(gatewaysRepo.findById(anyInt())).thenReturn(new Gateways());
        when(gatewaysRepo.save(any())).thenThrow(new RuntimeException());
        GatewaysDTO gatewaysDTO = new GatewaysDTO();
        gatewaysDTO.setSystemId("SystemID");
        ApiResponse response = gatewaysService.update(-1, gatewaysDTO);
        assertNotNull(response);
        assertEquals(500, response.status());
    }

    @Test
    void updateGatewayInRedisIfEditedTest() {
        when(gatewaysRepo.findById(anyInt())).thenReturn(new Gateways());
        GatewaysDTO gatewaysDTO = new GatewaysDTO();
        Gateways gateways = new Gateways();
        gateways.setNetworkId(10);
        gateways.setSystemId("SystemId3");
        gateways.setProtocol("SMPP");
        when(gatewaysMapper.toDTO(gateways)).thenReturn(new ParseGatewaysDTO());
        when(gatewaysRepo.findById(anyInt())).thenReturn(gateways);
        when(gatewaysRepo.save(any())).thenReturn(new Gateways());
        when(operatorRepo.findById(anyInt())).thenReturn(new OperatorMno());
        OperatorMno operatorMno = new OperatorMno();
        operatorMno.setTlvMessageReceiptId(Boolean.TRUE);
        operatorMno.setMessageIdDecimalFormat(Boolean.TRUE);
        ApiResponse response = gatewaysService.update(-1, gatewaysDTO);
        assertNotNull(response);
    }

    @Test
    void updateGatewayEnabledDefaultCaseTest() {
        when(gatewaysRepo.findById(anyInt())).thenReturn(new Gateways());
        GatewaysDTO gatewaysDTO = new GatewaysDTO();
        gatewaysDTO.setEnabled(3);
        Gateways gateways = new Gateways();
        gateways.setNetworkId(10);
        gateways.setSystemId("SystemId3");
        gateways.setProtocol("SMPP");
        when(gatewaysMapper.toDTO(gateways)).thenReturn(new ParseGatewaysDTO());
        when(gatewaysRepo.findById(anyInt())).thenReturn(gateways);
        when(gatewaysRepo.save(any())).thenReturn(new Gateways());
        when(operatorRepo.findById(anyInt())).thenReturn(new OperatorMno());
        OperatorMno operatorMno = new OperatorMno();
        operatorMno.setTlvMessageReceiptId(Boolean.TRUE);
        operatorMno.setMessageIdDecimalFormat(Boolean.TRUE);
        ApiResponse response = gatewaysService.update(-1, gatewaysDTO);
        assertNotNull(response);
    }

    void testGatewayEnabledNotPreviousTest(int enabledForDto, int enabledForEntity) {
        when(gatewaysRepo.findById(anyInt())).thenReturn(new Gateways());
        GatewaysDTO gatewaysDTO = new GatewaysDTO();
        gatewaysDTO.setEnabled(enabledForDto);
        Gateways gateways = new Gateways();
        gateways.setSystemId("SystemId");
        gateways.setEnabled(enabledForEntity);
        when(gatewaysMapper.toDTO(gateways)).thenReturn(new ParseGatewaysDTO());
        when(gatewaysRepo.findById(anyInt())).thenReturn(gateways);
        when(gatewaysRepo.save(any())).thenReturn(gateways);
        when(operatorRepo.findById(anyInt())).thenReturn(new OperatorMno());
        OperatorMno operatorMno = new OperatorMno();
        operatorMno.setTlvMessageReceiptId(Boolean.TRUE);
        operatorMno.setMessageIdDecimalFormat(Boolean.TRUE);
        ApiResponse response = gatewaysService.update(-1, gatewaysDTO);
        assertNotNull(response);
        assertEquals(200, response.status());
    }

    @Test
    void testGatewayEnabledNotPreviousWithZeroTest() {
        testGatewayEnabledNotPreviousTest(0, 1);
    }

    @Test
    void testGatewayEnabledNotPreviousWithOneTest() {
        testGatewayEnabledNotPreviousTest(1, 0);
    }

    @Test
    void testGatewayEnabledNotPreviousWithTwoTest() {
        testGatewayEnabledNotPreviousTest(2, 0);
    }

    @Test
    void onlyToLoadInitInRedisAndSocketTest() {
        Gateways gateways = new Gateways();
        gateways.setSystemId("SystemId");
        gateways.setNetworkId(1);
        when(gatewaysRepo.findById(anyInt())).thenReturn(gateways);
        when(operatorRepo.findById(anyInt())).thenReturn(new OperatorMno());
        when(gatewaysMapper.toDTO(gateways)).thenReturn(new ParseGatewaysDTO());
        assertDoesNotThrow(() -> gatewaysService.onlyToLoadInitInRedisAndSocket(gateways.getNetworkId()));
    }

    @Test
    void createGatewayFormatToRedisAnyExceptionTest() {
        when(gatewaysRepo.findById(anyInt())).thenThrow(new RuntimeException());
        assertNull(gatewaysService.createGatewayFormatToRedis(anyInt()));
    }

    @Test
    void updateAllGatewayByMnoTest() {
        when(gatewaysRepo.findByMnoIdAndEnabledNot(1, DELETED_STATUS)).thenReturn(List.of(new Gateways()));
        Gateways gateways = new Gateways();
        gateways.setSystemId("SystemId");
        gateways.setNetworkId(1);
        when(gatewaysRepo.findById(anyInt())).thenReturn(gateways);
        when(operatorRepo.findById(anyInt())).thenReturn(new OperatorMno());
        when(gatewaysMapper.toDTO(gateways)).thenReturn(new ParseGatewaysDTO());
        assertTrue(gatewaysService.updateAllGatewayByMno(1));
    }

    @Test
    void updateAllGatewayByMnoAnyExceptionTest() {
        when(gatewaysRepo.findByMnoIdAndEnabledNot(1, DELETED_STATUS)).thenThrow(new RuntimeException());
        assertFalse(gatewaysService.updateAllGatewayByMno(1));
    }

    @Test
    void createGatewayTransmitterTest() {
        doThrow(new IllegalArgumentException("you must disable the DLR request for the bind type TRANSMITTER"))
                .when(utilsBase).isRequestDlrAndTransmitterBind(anyString(), anyBoolean(), anyString());
        GatewaysDTO gatewaysDTO = new GatewaysDTO();
        gatewaysDTO.setSystemId("SystemID");
        gatewaysDTO.setBindType("TRANSMITTER");
        gatewaysDTO.setRequestDlr(true);
        gatewaysDTO.setProtocol("SMPP");
        ApiResponse response = gatewaysService.create(gatewaysDTO);
        assertEquals(500, response.status());
    }

    @Test
    void updateGatewayTransmitterTest() {
        doThrow(new IllegalArgumentException("you must disable the DLR request for the bind type TRANSMITTER"))
                .when(utilsBase).isRequestDlrAndTransmitterBind(anyString(), anyBoolean(), anyString());
        GatewaysDTO gatewaysDTO = new GatewaysDTO();
        gatewaysDTO.setPassword("passW00rD");
        gatewaysDTO.setSystemId("SystemID");
        gatewaysDTO.setBindType("TRANSMITTER");
        gatewaysDTO.setRequestDlr(true);
        gatewaysDTO.setProtocol("SMPP");
        ApiResponse response = gatewaysService.update(-1, gatewaysDTO);
        assertEquals(500, response.status());
    }
}
