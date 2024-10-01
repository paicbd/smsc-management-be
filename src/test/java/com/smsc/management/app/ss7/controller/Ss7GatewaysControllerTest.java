package com.smsc.management.app.ss7.controller;

import com.smsc.management.app.sequence.SequenceNetworksId;
import com.smsc.management.app.sequence.SequenceNetworksIdRepository;
import com.smsc.management.app.ss7.dto.Ss7GatewaysDTO;
import com.smsc.management.app.ss7.mapper.Ss7GatewaysMapper;
import com.smsc.management.app.ss7.model.entity.Ss7Gateways;
import com.smsc.management.app.ss7.model.repository.Ss7GatewaysRepository;
import com.smsc.management.app.ss7.utilsTest.Utils;
import com.smsc.management.integration.BaseIntegrationTest;
import com.smsc.management.utils.ApiResponse;
import com.smsc.management.utils.Constants;
import com.smsc.management.utils.UtilsBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;

import java.util.List;
import java.util.Objects;

import static com.smsc.management.app.ss7.utilsTest.Utils.checkAssertions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

class Ss7GatewaysControllerTest extends BaseIntegrationTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private Ss7GatewaysController ss7GatewaysController;

    @Autowired
    private Ss7GatewaysMapper ss7GatewaysMapper;

    @MockBean
    private Ss7GatewaysRepository ss7GatewaysRepository;

    @MockBean
    private SequenceNetworksIdRepository sequenceNetworksIdRepository;

    @MockBean
    private UtilsBase utilsBase;

    @AfterEach
    void tearDown() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "ss7_gateways");
    }

    @Test
    void getListGateway() {
        Ss7GatewaysDTO ss7GatewaysDTO = Utils.getSs7GatewaysDTO();
        Ss7Gateways ss7Gateways = ss7GatewaysMapper.toEntity(ss7GatewaysDTO);

        Mockito.when(ss7GatewaysRepository.findByEnabledNot(Constants.DELETED_STATUS)).thenReturn(List.of(ss7Gateways));
        ResponseEntity<ApiResponse> response = ss7GatewaysController.listGateway();
        checkAssertions(response, HttpStatus.OK);

        Mockito.doThrow(new RuntimeException("Test Exception")).when(ss7GatewaysRepository).findByEnabledNot(anyInt());
        response = ss7GatewaysController.listGateway();
        checkAssertions(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void createTest() {
        Ss7GatewaysDTO newSs7GatewaysDTO = Utils.newSs7GatewaysDTO();
        SequenceNetworksId newSeqGwMock = new SequenceNetworksId("GW");

        Ss7GatewaysDTO ss7GatewaysDTO = Utils.getSs7GatewaysDTO();
        Ss7Gateways ss7Gateways = ss7GatewaysMapper.toEntity(ss7GatewaysDTO);

        Mockito.when(sequenceNetworksIdRepository.save(any())).thenReturn(newSeqGwMock);
        Mockito.when(ss7GatewaysRepository.save(any())).thenReturn(ss7Gateways);
        ResponseEntity<ApiResponse> response = ss7GatewaysController.create(newSs7GatewaysDTO);
        checkAssertions(response, HttpStatus.OK);

        Mockito.doThrow(new RuntimeException("Test Exception")).when(ss7GatewaysRepository).save(any());
        response = ss7GatewaysController.create(newSs7GatewaysDTO);
        checkAssertions(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void updateTest() {
        Ss7GatewaysDTO ss7GatewaysDTOUpdated = Utils.getSs7GatewaysDTO();
        ss7GatewaysDTOUpdated.setName("updating Name");
        Ss7Gateways ss7Gateways = ss7GatewaysMapper.toEntity(ss7GatewaysDTOUpdated);

        Ss7GatewaysDTO ss7GatewaysDTO = Utils.getSs7GatewaysDTO();
        Ss7Gateways currentSs7Gateways = ss7GatewaysMapper.toEntity(ss7GatewaysDTO);

        Mockito.when(ss7GatewaysRepository.findById(anyInt())).thenReturn(currentSs7Gateways);
        Mockito.when(ss7GatewaysRepository.save(any())).thenReturn(ss7Gateways);
        ResponseEntity<ApiResponse> response = ss7GatewaysController.update(ss7GatewaysDTOUpdated, 1);
        checkAssertions(response, HttpStatus.OK);
        var apiResponse = (Ss7GatewaysDTO) Objects.requireNonNull(response.getBody()).data();
        assertNotNull(apiResponse);
        assertEquals("updating Name", apiResponse.getName());

        Mockito.doNothing().when(utilsBase).removeInRedis(anyString(), anyString());
        Mockito.doNothing().when(utilsBase).sendNotificationSocket(anyString(), anyString());
        ss7GatewaysDTOUpdated.setEnabled(Constants.DELETED_STATUS);
        ss7Gateways = ss7GatewaysMapper.toEntity(ss7GatewaysDTOUpdated);
        Mockito.when(ss7GatewaysRepository.save(any())).thenReturn(ss7Gateways);
        response = ss7GatewaysController.update(ss7GatewaysDTOUpdated, 1);
        checkAssertions(response, HttpStatus.OK);
        apiResponse = (Ss7GatewaysDTO) Objects.requireNonNull(response.getBody()).data();
        assertNotNull(apiResponse);
        assertEquals("updating Name", apiResponse.getName());

        currentSs7Gateways.setEnabled(Constants.DELETED_STATUS);
        Mockito.when(ss7GatewaysRepository.findById(anyInt())).thenReturn(currentSs7Gateways);
        response = ss7GatewaysController.update(ss7GatewaysDTOUpdated, 1);
        checkAssertions(response, HttpStatus.BAD_REQUEST);

        Mockito.when(ss7GatewaysRepository.findById(anyInt())).thenReturn(null);
        response = ss7GatewaysController.update(ss7GatewaysDTOUpdated, 1);
        checkAssertions(response, HttpStatus.NOT_FOUND);

        Mockito.doThrow(new RuntimeException("Test Exception")).when(ss7GatewaysRepository).findById(anyInt());
        response = ss7GatewaysController.update(ss7GatewaysDTOUpdated, 1);
        checkAssertions(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void getGatewaysByNetworkId() {
        Ss7GatewaysDTO ss7GatewaysDTO = Utils.getSs7GatewaysDTO();
        Ss7Gateways currentSs7Gateways = ss7GatewaysMapper.toEntity(ss7GatewaysDTO);

        Mockito.when(ss7GatewaysRepository.findById(anyInt())).thenReturn(currentSs7Gateways);
        ResponseEntity<ApiResponse> response = ss7GatewaysController.getGateway(1);
        checkAssertions(response, HttpStatus.OK);

        Mockito.when(ss7GatewaysRepository.findById(anyInt())).thenReturn(null);
        response = ss7GatewaysController.getGateway(1);
        checkAssertions(response, HttpStatus.NOT_FOUND);

        Mockito.doThrow(new RuntimeException("Test Exception")).when(ss7GatewaysRepository).findById(anyInt());
        response = ss7GatewaysController.getGateway(1);
        checkAssertions(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
