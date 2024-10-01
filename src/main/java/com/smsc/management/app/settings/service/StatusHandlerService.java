package com.smsc.management.app.settings.service;


import com.paicbd.smsc.utils.Converter;
import com.paicbd.smsc.utils.Generated;
import com.smsc.management.utils.ApiResponse;
import com.smsc.management.app.settings.dto.ServerPropertiesDTO;
import com.smsc.management.app.settings.dto.ServerSmppPropertiesDTO;
import com.smsc.management.app.gateway.model.entity.Gateways;
import com.smsc.management.app.provider.model.entity.ServiceProvider;
import com.smsc.management.app.gateway.model.repository.GatewaysRepository;
import com.smsc.management.app.provider.model.repository.ServiceProviderRepository;
import com.smsc.management.utils.AppProperties;
import com.smsc.management.utils.ResponseMapping;
import com.smsc.management.utils.UtilsBase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisCluster;

import static com.smsc.management.app.settings.service.StatusHandlerService.Status.STOPPED;
import static com.smsc.management.app.settings.service.StatusHandlerService.UpdateParam.SESSIONS;
import static com.smsc.management.app.settings.service.StatusHandlerService.UpdateParam.STATUS;
import static com.smsc.management.utils.Constants.UPDATE_SERVER_HANDLER_ENDPOINT;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.smsc.management.utils.Constants.UPDATE_HTTP_SERVER_HANDLER_ENDPOINT;

/**
 * Service responsible for handling status updates for service providers, gateways,
 * and servers.
 */
@Generated // Waiting for networkId handling implementation
@Service
@RequiredArgsConstructor
@Slf4j(topic = "StatusHandlerService")
public class StatusHandlerService {
    private final ServiceProviderRepository spRepository;
    private final GatewaysRepository gatewaysRepository;
    private final JedisCluster jedisCluster;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final UtilsBase utilsBase;
    private final AppProperties appProperties;


    enum Status {
        STOPPED,
        STARTED,
        BINDING,
        BOUND,
        UNBINDING,
    }

    enum UpdateParam {
        STATUS,
        SESSIONS
    }

    /**
     * Updates the status of a service provider in the database.
     *
     * @param systemId The system ID of the service provider
     * @param param    The parameter to update
     * @param value    The new value
     */
    public void updateSpStatusOnDatabase(String systemId, String param, String value) {
        log.info("Handling service provider status: {}", systemId);
        ServiceProvider serviceProvider = findServiceProvider(systemId);
        if (serviceProvider == null) {
            return;
        }

        try {
            if (STATUS.toString().equals(param.toUpperCase())) {
                if (STOPPED.toString().equals(value.toUpperCase())) {
                    spRepository.saveSessionsSp(serviceProvider.getNetworkId(), 0);
                }
                spRepository.saveStatusSp(serviceProvider.getNetworkId(), value.toUpperCase());
                log.info("Service Provider {} status updated", systemId);
            } else if (SESSIONS.toString().equals(param.toUpperCase())) {
                spRepository.saveSessionsSp(serviceProvider.getNetworkId(), Integer.parseInt(value));
            } else {
                log.error("Invalid param on update service provider status: {}", param);
            }
        } catch (IllegalArgumentException e) {
            log.error("An error has occurred on update: {}", e.getMessage());
        }
    }

    /**
     * Updates the status of a gateway in the database.
     *
     * @param systemId The system ID of the gateway
     * @param param    The parameter to update
     * @param value    The new value
     */
    public void updateGwStatusOnDatabase(String systemId, String param, String value) {
        log.info("Handling gateway status: {}", systemId);
        Gateways gateway = findGateway(systemId);
        if (gateway == null) {
            return;
        }

        try {
            if (STATUS.toString().equals(param.toUpperCase())) {
                if (STOPPED.toString().equals(value.toUpperCase())) {
                    gatewaysRepository.saveSessionsGateway(gateway.getNetworkId(), 0);
                }
                gatewaysRepository.saveStatusGateway(gateway.getNetworkId(), value.toUpperCase());
                log.info("Gateway {} status updated", systemId);
            } else if (SESSIONS.toString().equals(param.toUpperCase())) {
                gatewaysRepository.saveSessionsGateway(gateway.getNetworkId(), Integer.parseInt(value));
            } else {
                log.error("Invalid param: {}", param);
            }
        } catch (IllegalArgumentException e) {
            log.error("An error has occurred: {}", e.getMessage());
        }
    }

    /**
     * Finds a service provider by its system ID.
     *
     * @param systemId The system ID of the service provider to find
     * @return The service provider if found, null otherwise
     */
    private ServiceProvider findServiceProvider(String systemId) {
        ServiceProvider serviceProvider = spRepository.findBySystemId(systemId);
        if (serviceProvider == null) {
            log.error("Service provider with system id {} not found", systemId);
        }
        return serviceProvider;
    }

    /**
     * Finds a gateway by its system ID.
     *
     * @param systemId The system ID of the gateway to find
     * @return The gateway if found, null otherwise
     */
    private Gateways findGateway(String systemId) {
        Gateways gateway = gatewaysRepository.findBySystemIdAndEnabledNot(systemId, 2); // Enabled = 2 means that the gateway is not deleted
        if (gateway == null) {
            log.error("Gateway with system id {} not found", systemId);
        }
        return gateway;
    }

    /**
     * This method is used to update the status of the server in redis and send a notification to the backend app via websocket.
     *
     * @param status The new status of the server
     * @return True if the update is successful, false otherwise
     */
    public boolean updateStatusSmppServer(String status) {
        log.info("Handling status server: {}", status);
        ServerSmppPropertiesDTO serverPropertiesDTO = getSmppServerHandler();

        if (serverPropertiesDTO == null) {
            log.error("The configuration of the server was not found in redis");
            return false;
        }

        serverPropertiesDTO.setState(status);
        jedisCluster.hset(this.appProperties.getConfigurationHash(), this.appProperties.getServerKey(), Converter.valueAsString(serverPropertiesDTO));
        simpMessagingTemplate.convertAndSend(UPDATE_SERVER_HANDLER_ENDPOINT, serverPropertiesDTO.getState());
        return true;
    }

    /**
     * Retrieves the server properties from Redis.
     *
     * @return The server properties if found, null otherwise
     */
    private ServerSmppPropertiesDTO getSmppServerHandler() {
        String serverHandlerJson = utilsBase.getInRedis(this.appProperties.getConfigurationHash(), this.appProperties.getServerKey());
        if (serverHandlerJson == null) {
            log.error("ServerHandler not found in redis");
            return null;
        }

        try {
            return Converter.stringToObject(serverHandlerJson, ServerSmppPropertiesDTO.class);
        } catch (Exception e) {
            log.error("Error on getServerHandler: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Retrieves the configuration of the SMPP server.
     *
     * @return ResponseDTO containing the SMPP server configurations
     */
    public ApiResponse getSmppServerConfiguration() {
        try {
            String serverHandlerJson = utilsBase.getInRedis(this.appProperties.getConfigurationHash(), this.appProperties.getServerKey());
            if (serverHandlerJson == null) {
                log.error("SMPP server configuration was not found in redis");
                return ResponseMapping.errorMessageNoFound("ServerHandler not found in Redis");
            }
            ServerSmppPropertiesDTO resultDTO = Converter.stringToObject(serverHandlerJson, ServerSmppPropertiesDTO.class);
            return ResponseMapping.successMessage("Get SMPP server configurations successfully.", resultDTO);
        } catch (Exception e) {
            log.error("get SMPP server configurations request: {}", e.getMessage());
            return ResponseMapping.exceptionMessage("get SMPP server configurations request with error", e);
        }
    }

    /**
     * Updates the status of an HTTP server in the Redis.
     *
     * @param applicationName The name of the HTTP server application.
     * @param status          The new status to set for the HTTP server.
     * @return A ResponseDTO indicating the result of the status update operation.
     * If successful, returns a success message along with the updated server properties.
     * If the instance is not found in Redis, returns an error message indicating the absence.
     * If an exception occurs during the update process, returns an exception message.
     */
    public ApiResponse updateStatusServerHttp(String applicationName, String status) {
        try {
            String redisDataStr = utilsBase.getInRedis(this.appProperties.getConfigurationHash(), applicationName);
            if (redisDataStr == null) {
                return ResponseMapping.errorMessageNoFound("Instance " + applicationName + " was not found");
            }

            // update server status
            ServerPropertiesDTO httpServerPropertiesDTO = Converter.stringToObject(redisDataStr, ServerPropertiesDTO.class);
            httpServerPropertiesDTO.setState(status);
            jedisCluster.hset(this.appProperties.getConfigurationHash(), applicationName, Converter.valueAsString(httpServerPropertiesDTO));

            // notify cors http per socket.
            simpMessagingTemplate.convertAndSend(UPDATE_HTTP_SERVER_HANDLER_ENDPOINT, httpServerPropertiesDTO.getName());

            return ResponseMapping.successMessage("successful state change", httpServerPropertiesDTO);
        } catch (Exception e) {
            log.error("Error to update status http server {}", e.getMessage(), e);
            return ResponseMapping.exceptionMessage("Error to update status http server", e);
        }
    }

    /**
     * Updates the status of all HTTP servers in Redis.
     *
     * @param newStatus The new status to set for all HTTP servers.
     * @return A {@link ApiResponse} indicating the result of the operation.
     */
    public ApiResponse updateStatusAllServerHttp(String newStatus) {
        try {
            // Retrieve all HTTP servers from Redis
            Map<String, String> mapHttpServers = utilsBase.getAllInRedis(this.appProperties.getConfigurationHash());

            // update server status
            int instancesUpdates = 0;
            for (Map.Entry<String, String> entry : mapHttpServers.entrySet()) {
                ServerPropertiesDTO httpServerPropertiesDTO = Converter.stringToObject(entry.getValue(), ServerPropertiesDTO.class);
                if (httpServerPropertiesDTO.getProtocol() != null && httpServerPropertiesDTO.getProtocol().equalsIgnoreCase("http")) {
                    httpServerPropertiesDTO.setState(newStatus);
                    jedisCluster.hset(this.appProperties.getConfigurationHash(), httpServerPropertiesDTO.getName(), Converter.valueAsString(httpServerPropertiesDTO));
                    simpMessagingTemplate.convertAndSend(UPDATE_HTTP_SERVER_HANDLER_ENDPOINT, httpServerPropertiesDTO.getName());
                    instancesUpdates++;
                }
            }

            if (instancesUpdates == 0) {
                return ResponseMapping.errorMessageNoFound("Instances HTTP were not found");
            }

            return ResponseMapping.successMessage("Updated to the new " + newStatus + " state at " + instancesUpdates + " HTTP instances successfully.", null);
        } catch (Exception e) {
            log.error("Error to update status to all http server -> {}", e.getMessage(), e);
            return ResponseMapping.exceptionMessage("Error to update status to all http server", e);
        }
    }

    /**
     * Retrieves information about all HTTP servers stored in Redis.
     *
     * @return A ResponseDTO containing information about all HTTP servers.
     * If HTTP server instances are found in Redis, returns a success message
     * along with the list of HTTP server properties.
     * If no HTTP server instances are found in Redis, returns an error message.
     * If an exception occurs during the retrieval process, returns an exception message.
     */
    public ApiResponse getAllHttpServers() {
        try {
            Map<String, String> mapHttpServers = utilsBase.getAllInRedis(this.appProperties.getConfigurationHash());
            List<ServerPropertiesDTO> listHttpServers = new ArrayList<>();
            for (Map.Entry<String, String> entry : mapHttpServers.entrySet()) {
                ServerPropertiesDTO resultDTO = Converter.stringToObject(entry.getValue(), ServerPropertiesDTO.class);
                if (resultDTO.getProtocol() != null && resultDTO.getProtocol().equalsIgnoreCase("http")) {
                    listHttpServers.add(resultDTO);
                }
            }

            if (listHttpServers.isEmpty()) {
                return ResponseMapping.errorMessageNoFound("Instances HTTP were not found");
            }

            return ResponseMapping.successMessage("Get HTTP Servers successfully.", listHttpServers);
        } catch (Exception e) {
            log.error("Error on get HTTP Servers request -> {}", e.getMessage(), e);
            return ResponseMapping.exceptionMessage("get HTTP Servers request with error", e);
        }
    }
}

