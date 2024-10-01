package com.smsc.management.app.routing.controller;

import com.smsc.management.app.routing.dto.RoutingRulesDTO;
import com.smsc.management.app.routing.dto.RoutingRulesDestinationDTO;
import com.smsc.management.app.sequence.SequenceNetworksId;
import com.smsc.management.app.sequence.SequenceNetworksIdRepository;
import com.smsc.management.integration.BaseIntegrationTest;
import com.smsc.management.utils.ApiResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class RoutingRuleControllerTest extends BaseIntegrationTest {

    @Autowired
    private RoutingRuleController routingRuleController;

    @Autowired
    private SequenceNetworksIdRepository sequenceNetworksIdRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @AfterEach
    void tearDown() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "routing_rules_destination");
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "routing_rules");
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "sequence_networks_id");
    }

    @Test
    void listErrorCodeTest() {
        ApiResponse response = routingRuleController.listErrorCode().getBody();
        assertNotNull(response);
        assertEquals(200, response.status());
        assertInstanceOf(ApiResponse.class, response);
        assertInstanceOf(List.class, response.data());
    }

    @Test
    void getNetworksTest() {
        ApiResponse response = routingRuleController.getNetworks().getBody();
        assertNotNull(response);
        assertEquals(200, response.status());
        assertInstanceOf(ApiResponse.class, response);
        assertInstanceOf(List.class, response.data());
    }

    @Test
    void createRoutingRuleTest() {
        RoutingRulesDTO routingRulesDTO = createAndGetMockRoutingRuleDto();
        ApiResponse response = routingRuleController.create(routingRulesDTO).getBody();
        assertNotNull(response);
        assertEquals(200, response.status());
    }

    @Test
    void updateRoutingRuleTest() {
        RoutingRulesDTO routingRulesDTO = createAndGetMockRoutingRuleDto();
        routingRuleController.create(routingRulesDTO);
        routingRulesDTO.setDropTempFailure(false);
        ApiResponse response = routingRuleController.update(routingRulesDTO, routingRulesDTO.getId()).getBody();
        assertNotNull(response);
        assertEquals(200, response.status());
    }

    @Test
    void deleteRoutingRuleTest() {
        RoutingRulesDTO routingRulesDTO = createAndGetMockRoutingRuleDto();
        RoutingRulesDTO newRouting = (RoutingRulesDTO) Objects.requireNonNull(routingRuleController.create(routingRulesDTO).getBody()).data();
        ApiResponse response = routingRuleController.delete(newRouting.getId()).getBody();
        assertNotNull(response);
        assertEquals(200, response.status());
    }

    private RoutingRulesDTO createAndGetMockRoutingRuleDto() {
        RoutingRulesDTO routingRulesDTO = new RoutingRulesDTO();
        List<RoutingRulesDestinationDTO> destinationsMock = createAndGetMockRoutingRuleDestinationDto();
        routingRulesDTO.setOriginNetworkId(destinationsMock.getFirst().getNetworkId());
        routingRulesDTO.setDestination(createAndGetMockRoutingRuleDestinationDto());
        return routingRulesDTO;
    }

    private List<RoutingRulesDestinationDTO> createAndGetMockRoutingRuleDestinationDto() {
        List<RoutingRulesDestinationDTO> destinationDTOS = new ArrayList<>();
        RoutingRulesDestinationDTO routingRulesDestinationDTO = new RoutingRulesDestinationDTO();
        SequenceNetworksId sequenceNetworksId = createAndGetMockSequenceNetwork();
        routingRulesDestinationDTO.setRoutingRulesId(5);
        routingRulesDestinationDTO.setName("Origin");
        routingRulesDestinationDTO.setPriority(1);
        routingRulesDestinationDTO.setAction(1);
        routingRulesDestinationDTO.setNetworkId(sequenceNetworksId.getId());
        routingRulesDestinationDTO.setNetworkType("Type");
        destinationDTOS.add(routingRulesDestinationDTO);
        return destinationDTOS;
    }

    private SequenceNetworksId createAndGetMockSequenceNetwork() {
        SequenceNetworksId sequenceNetworksId = new SequenceNetworksId();
        sequenceNetworksId.setNetworkType("NET");
        return sequenceNetworksIdRepository.save(sequenceNetworksId);
    }
}
