package com.smsc.management.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smsc.management.app.errorcode.dto.ParseErrorCodeMappingDTO;
import com.smsc.management.app.routing.dto.RedisRoutingRulesDTO;
import com.smsc.management.app.routing.dto.RedisRoutingRulesDestinationDTO;
import com.smsc.management.app.routing.dto.RoutingRulesDTO;
import com.smsc.management.app.routing.dto.RoutingRulesDestinationDTO;
import com.smsc.management.app.routing.model.entity.RoutingRules;
import com.smsc.management.app.sequence.SequenceNetworksId;
import com.smsc.management.app.settings.model.entity.CommonVariables;
import com.smsc.management.app.settings.model.repository.CommonVariablesRepository;
import com.smsc.management.exception.InvalidStructureException;
import com.smsc.management.app.routing.mapper.RoutingRulesMapper;
import com.smsc.management.app.routing.model.repository.RoutingRulesDestinationRepository;
import com.smsc.management.app.routing.model.repository.RoutingRulesRepository;
import com.smsc.management.app.sequence.SequenceNetworksIdRepository;

import com.smsc.management.exception.SmscBackendException;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import redis.clients.jedis.JedisCluster;

import static com.smsc.management.utils.Constants.CONNECT_GATEWAY_ENDPOINT;
import static com.smsc.management.utils.Constants.CONNECT_HTTP_GATEWAY_ENDPOINT;
import static com.smsc.management.utils.Constants.DELETE_GATEWAY_ENDPOINT;
import static com.smsc.management.utils.Constants.DELETE_HTTP_GATEWAY_ENDPOINT;
import static com.smsc.management.utils.Constants.DELETE_SERVICE_HTTP_PROVIDER_ENDPOINT;
import static com.smsc.management.utils.Constants.DELETE_SERVICE_SMPP_PROVIDER_ENDPOINT;
import static com.smsc.management.utils.Constants.KEY_MAX_PASSWORD_LENGTH;
import static com.smsc.management.utils.Constants.KEY_MAX_SYSTEM_ID_LENGTH;
import static com.smsc.management.utils.Constants.SMSC_ACCOUNT_SETTINGS;
import static com.smsc.management.utils.Constants.STOP_GATEWAY_ENDPOINT;
import static com.smsc.management.utils.Constants.STOP_HTTP_GATEWAY_ENDPOINT;
import static com.smsc.management.utils.Constants.UPDATE_GATEWAY_ENDPOINT;
import static com.smsc.management.utils.Constants.UPDATE_HTTP_GATEWAY_ENDPOINT;
import static com.smsc.management.utils.Constants.UPDATE_SERVICE_HTTP_PROVIDER_ENDPOINT;
import static com.smsc.management.utils.Constants.UPDATE_SERVICE_SMPP_PROVIDER_ENDPOINT;

/**
 * Utility class that provides methods for various processes.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UtilsBase {
    private final JedisCluster jedisCluster;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final RoutingRulesRepository routingRulesRepo;
    private final RoutingRulesDestinationRepository routingRulesDestRepo;
    private final SequenceNetworksIdRepository sequenceNetworkRepo;
    private final CommonVariablesRepository commonVariablesRepository;
    private final RoutingRulesMapper routingRulesMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Map<Integer, String> DEFAULT_ENDPOINTS = Map.of(
            0, STOP_GATEWAY_ENDPOINT,
            1, CONNECT_GATEWAY_ENDPOINT,
            2, DELETE_GATEWAY_ENDPOINT
    );

    private static final Map<Integer, String> HTTP_ENDPOINTS = Map.of(
            0, STOP_HTTP_GATEWAY_ENDPOINT,
            1, CONNECT_HTTP_GATEWAY_ENDPOINT,
            2, DELETE_HTTP_GATEWAY_ENDPOINT
    );

    /**
     * Retrieves error code mappings from a list of maps.
     *
     * @param mapData The list of maps containing error code mapping data
     * @return The list of parsed error code mapping DTOs
     */
    public List<ParseErrorCodeMappingDTO> getErrorCodeMappings(List<Map<String, Object>> mapData) {
        List<ParseErrorCodeMappingDTO> dtoList = new ArrayList<>();

        for (Map<String, Object> result : mapData) {
            int id = (int) result.get("errorCodeMappingId");
            int operatorMnoId = (int) result.get("operatorMnoId");
            String operatorMno = (String) result.get("operatorMno");
            int providerErrorCodeId = (int) result.get("errorCodeId");
            String providerErrorCode = (String) result.get("errorCode");
            int serverErrorCodeId = (int) result.get("deliveryErrorCodeId");
            String serverErrorCode = (String) result.get("deliveryErrorCode");
            String deliveryStatus = (String) result.get("deliveryStatus");

            var dto = new ParseErrorCodeMappingDTO(
                    id, operatorMnoId, operatorMno, providerErrorCodeId, providerErrorCode,
                    serverErrorCodeId, serverErrorCode, deliveryStatus
            );
            dtoList.add(dto);
        }

        return dtoList;
    }

    /**
     * Validates destination rules for routing.
     *
     * @param destinationRules The list of routing rules destination DTOs to validate
     * @throws InvalidStructureException if the destination rules are invalid
     */
    public void validateDestinationRules(List<RoutingRulesDestinationDTO> destinationRules) throws InvalidStructureException {
        Set<Integer> priorities = new HashSet<>();
        Set<Integer> networkIds = new HashSet<>();

        for (RoutingRulesDestinationDTO destination : destinationRules) {
            if (destination.getAction() == 1) {
                continue;
            }

            boolean isPriorityAdded = priorities.add(destination.getPriority());
            boolean isNetworkIdAdded = networkIds.add(destination.getNetworkId());

            if (!isPriorityAdded) {
                throw new InvalidStructureException("The priority " + destination.getPriority() + " is duplicated");
            }

            if (!isNetworkIdAdded) {
                throw new InvalidStructureException("The network_id " + destination.getNetworkId() + " is duplicated");
            }
        }
    }


    /**
     * Validates network ID parameters in a RoutingRulesDTO object.
     * This method throws an exception if specific routing rule options are enabled but their
     * corresponding network ID parameters are missing. It ensures proper configuration for
     * message routing based on network selection.
     *
     * @param routingRulesDTO The RoutingRulesDTO object containing the routing rules to be validated.
     */
    public void validateNetworksParameter(RoutingRulesDTO routingRulesDTO) {
        boolean isDropTempFailure = routingRulesDTO.isDropTempFailure();
        boolean networkIdTempFailureIsNull = routingRulesDTO.getNetworkIdTempFailure() == null;
        if (isDropTempFailure && networkIdTempFailureIsNull) {
            throw new SmscBackendException("To use drop after temporary failure you must select a valid network id");
        }
    }

    /**
     * Stores data in Redis.
     *
     * @param hashName The name of the Redis hash
     * @param key      The key to store the data
     * @param value    The data to store
     */
    public void storeInRedis(String hashName, String key, String value) {
        try {
            jedisCluster.hset(hashName, key, value);
        } catch (Exception e) {
            log.error("storeInRedis with error: {}", e.getMessage());
        }
    }

    /**
     * Removes data from Redis.
     *
     * @param hashName The name of the Redis hash
     * @param key      The key of the data to remove
     */
    public void removeInRedis(String hashName, String key) {
        try {
            jedisCluster.hdel(hashName, key);
        } catch (Exception e) {
            log.error("removeInRedis with error: {}", e.getMessage());
        }
    }

    /**
     * Retrieves data from Redis.
     *
     * @param hashName The name of the Redis hash
     * @param key      The key to retrieve the data
     * @return The retrieved data if available, null otherwise
     */
    public String getInRedis(String hashName, String key) {
        try {
            return jedisCluster.hget(hashName, key);
        } catch (Exception e) {
            log.error("getInRedis with error: {}", e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves all key-value pairs stored in a Redis hash.
     *
     * @param hashName the name of the Redis hash
     * @return a {@code Map<String, String>} containing all key-value pairs in the specified hash;
     * {@code null} if an error occurs during retrieval
     */
    public Map<String, String> getAllInRedis(String hashName) {
        try {
            return jedisCluster.hgetAll(hashName);
        } catch (Exception e) {
            log.error("Error while trying to extract data from hash in Redis {}", e.getMessage());
        }
        return Collections.emptyMap();
    }

    /**
     * Sends a notification via WebSocket.
     *
     * @param endpoint The WebSocket endpoint
     * @param data     The data to send
     */
    public void sendNotificationSocket(String endpoint, String data) {
        try {
            simpMessagingTemplate.convertAndSend(endpoint, data);
        } catch (Exception e) {
            log.error("sendNotificationSocket with error: {}", e.getMessage());
        }
    }

    /**
     * Finds the deletion endpoint by protocol for a service provider.
     *
     * @param protocol The protocol used by the service provider.
     * @return The deletion endpoint corresponding to the specified protocol.
     */
    public String findDeleteEndpointByProtocolToSP(String protocol) {
        if ("http".equalsIgnoreCase(protocol)) {
            return DELETE_SERVICE_HTTP_PROVIDER_ENDPOINT;
        }
        return DELETE_SERVICE_SMPP_PROVIDER_ENDPOINT;
    }

    /**
     * Finds the update endpoint by protocol for a service provider.
     *
     * @param protocol The protocol used by the service provider.
     * @return The update endpoint corresponding to the specified protocol.
     */
    public String findUpdateEndpointByProtocolToSP(String protocol) {
        if ("http".equalsIgnoreCase(protocol)) {
            return UPDATE_SERVICE_HTTP_PROVIDER_ENDPOINT;
        }
        return UPDATE_SERVICE_SMPP_PROVIDER_ENDPOINT;
    }

    public String findUpdateEndpointByProtocolToGw(String protocol) {
        if ("http".equalsIgnoreCase(protocol)) {
            return UPDATE_HTTP_GATEWAY_ENDPOINT;
        }
        return UPDATE_GATEWAY_ENDPOINT;
    }

    public String findDeleteEndpointByProtocolToGw(String protocol) {
        if ("http".equalsIgnoreCase(protocol)) {
            return DELETE_HTTP_GATEWAY_ENDPOINT;
        }
        return DELETE_GATEWAY_ENDPOINT;
    }

    public String findEndpointToGateway(String protocol, int action) {
        Map<Integer, String> endpoints = "http".equalsIgnoreCase(protocol) ? HTTP_ENDPOINTS : DEFAULT_ENDPOINTS;
        String endpoint = endpoints.get(action);

        if (endpoint == null) {
            throw new SmscBackendException("Endpoint to send request by socket was not found for gateways.");
        }

        return endpoint;
    }

    /**
     * Checks if the provided routing rules contain filter rules.
     * Filter rules are considered to be present if any of the following fields are not blank or true:
     * - regexSourceAddr
     * - regexSourceAddrTon
     * - regexSourceAddrNpi
     * - regexDestinationAddr
     * - regexDestAddrTon
     * - regexDestAddrNpi
     * - regexImsiDigitsMask
     * - regexNetworkNodeNumber
     * - regexCallingPartyAddress
     * - checkSriResponse
     *
     * @param routingRuleDTO The routing rules to check.
     * @return {@code true} if source rules are present, {@code false} otherwise.
     */
    public boolean hasFilterRules(RedisRoutingRulesDTO routingRuleDTO) {
        return Stream.of(routingRuleDTO.getRegexSourceAddr(),
                        routingRuleDTO.getRegexSourceAddrTon(),
                        routingRuleDTO.getRegexSourceAddrNpi(),
                        routingRuleDTO.getRegexDestinationAddr(),
                        routingRuleDTO.getRegexDestAddrTon(),
                        routingRuleDTO.getRegexDestAddrNpi(),
                        routingRuleDTO.getRegexImsiDigitsMask(),
                        routingRuleDTO.getRegexNetworkNodeNumber(),
                        routingRuleDTO.getRegexCallingPartyAddress())
                       .anyMatch(this::isNotBlank) || routingRuleDTO.isSriResponse();
    }

    /**
     * Checks if the provided routing rules contain action rules.
     * Action rules are considered to be present if any of the following fields are not blank or -1:
     * - newSourceAddr
     * - newSourceAddrTon
     * - newSourceAddrNpi
     * - newDestinationAddr
     * - newDestAddrTon
     * - newDestAddrNpi
     * - addSourceAddrPrefix
     * - removeDestAddrPrefix
     * - newGtSccpAddrMt
     * - checkSriResponse
     *
     * @param routingRuleDTO The routing rules to check.
     * @return {@code true} if action rules are present, {@code false} otherwise.
     */
    public boolean hasActionRules(RedisRoutingRulesDTO routingRuleDTO) {
        return Stream.of(
                routingRuleDTO.getNewSourceAddr(),
                routingRuleDTO.getNewDestinationAddr(),
                routingRuleDTO.getAddSourceAddrPrefix(),
                routingRuleDTO.getAddDestAddrPrefix(),
                routingRuleDTO.getRemoveDestAddrPrefix(),
                routingRuleDTO.getNewGtSccpAddrMt()
        ).anyMatch(this::isNotBlank) ||
               Stream.of(
                       routingRuleDTO.getNewSourceAddrTon(),
                       routingRuleDTO.getNewSourceAddrNpi(),
                       routingRuleDTO.getNewDestAddrTon(),
                       routingRuleDTO.getNewDestAddrNpi()
               ).anyMatch(value -> value != -1) ||
               routingRuleDTO.isCheckSriResponse();
    }

    private boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
    }

    /**
     * Retrieves the routing rules for the specified network ID and protocol.
     *
     * @param networkId The network ID for which to retrieve the routing rules.
     * @param protocol  The protocol for which to retrieve the routing rules.
     * @return A list of RedisRoutingRulesDTO objects containing the retrieved routing rules.
     * Returns {@code null} if an error occurs during retrieval.
     */
    public List<RedisRoutingRulesDTO> getRoutingRules(int networkId, String protocol) {
        List<RedisRoutingRulesDTO> routingRules = new ArrayList<>();
        try {
            List<RoutingRules> routingRulesEntity = routingRulesRepo.findByOriginNetworkId(networkId);
            SequenceNetworksId seqNet = sequenceNetworkRepo.findById(networkId);

            for (RoutingRules routingRule : routingRulesEntity) {
                List<RedisRoutingRulesDestinationDTO> routingRulesDestinations = routingRulesDestRepo.findByRoutingRulesIdDTO(routingRule.getId());
                RedisRoutingRulesDTO routingRuleDTO = routingRulesMapper.toRedisDTO(routingRule);

                // set routing rules
                routingRuleDTO.setDestination(routingRulesDestinations);

                // set -1 networkId with null value
                if (routingRule.getNetworkIdToMapSri() == null) {
                    routingRuleDTO.setNetworkIdToMapSri(-1);
                }
                if (routingRule.getNetworkIdToPermanentFailure() == null) {
                    routingRuleDTO.setNetworkIdToPermanentFailure(-1);
                }
                if (routingRule.getNetworkIdTempFailure() == null) {
                    routingRuleDTO.setNetworkIdTempFailure(-1);
                }

                routingRuleDTO.setOriginProtocol(protocol);
                routingRuleDTO.setHasFilterRules(this.hasFilterRules(routingRuleDTO));
                routingRuleDTO.setHasActionRules(this.hasActionRules(routingRuleDTO));
                routingRuleDTO.setOriginNetworkType(seqNet.getNetworkType());
                routingRules.add(routingRuleDTO);
            }
        } catch (Exception e) {
            log.error("Error to get routing rules -> {}", e.getMessage());
        }
        return routingRules;
    }

    /**
     * Validates the length of the provided password and system ID based on the maximum allowed lengths
     * specified in the general settings.
     * <p>
     * This method checks if the password and system ID are non-blank and within the allowed length limits.
     * If either exceeds the maximum allowed length or is blank, an exception is thrown.
     * </p>
     *
     * @param passwd   the password to validate
     * @param systemId the system ID to validate
     * @throws JsonProcessingException  if there is an error parsing the JSON configuration retrieved from the database.
     * @throws SmscBackendException     if the configuration for the maximum password length is not found.
     * @throws IllegalArgumentException if the password or system ID is blank or exceeds the maximum allowed length.
     */
    public void validateMaxLengthSpAndGw(String passwd, String systemId, String protocol) throws JsonProcessingException {
        CommonVariables data = commonVariablesRepository.findByKey(SMSC_ACCOUNT_SETTINGS);
        if (Objects.isNull(data)) {
            throw new SmscBackendException("max password length setting was not found");
        }
        JsonNode jsonNode = objectMapper.readTree(data.getValue());

        if ("SMPP".equalsIgnoreCase(protocol)) {
            int maxPasswd = jsonNode.get(KEY_MAX_PASSWORD_LENGTH).asInt();
            if (passwd.isBlank() || passwd.length() > maxPasswd) {
                throw new IllegalArgumentException("Password must be between 1 and " + maxPasswd + " characters");
            }
        }

        int maxSystemId = jsonNode.get(KEY_MAX_SYSTEM_ID_LENGTH).asInt();
        if (systemId.isBlank() || systemId.length() > maxSystemId) {
            throw new IllegalArgumentException("System id must be between 1 and " + maxSystemId + " characters");
        }
    }

    public void isRequestDlrAndTransmitterBind(String bindType, boolean isRequestDrl, String protocol) {
        if ("TRANSMITTER".equalsIgnoreCase(bindType) && isRequestDrl && "SMPP".equalsIgnoreCase(protocol)) {
            throw new IllegalArgumentException("you must disable the DLR request for the bind type TRANSMITTER");
        }
    }
}
