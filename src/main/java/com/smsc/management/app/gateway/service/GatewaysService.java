package com.smsc.management.app.gateway.service;

import java.util.List;
import java.util.Objects;

import com.smsc.management.utils.AppProperties;
import com.smsc.management.utils.UtilsBase;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.smsc.management.app.gateway.dto.GatewaysDTO;
import com.smsc.management.app.gateway.dto.ParseGatewaysDTO;
import com.smsc.management.utils.ApiResponse;
import com.smsc.management.app.gateway.model.entity.Gateways;
import com.smsc.management.app.mno.model.entity.OperatorMno;
import com.smsc.management.app.gateway.mapper.GatewaysMapper;
import com.smsc.management.app.gateway.model.repository.GatewaysRepository;
import com.smsc.management.app.mno.model.repository.OperatorMnoRepository;
import com.smsc.management.utils.Constants;
import com.smsc.management.utils.ResponseMapping;
import com.smsc.management.app.sequence.SequenceNetworksIdGenerator;

import lombok.extern.slf4j.Slf4j;

import static com.smsc.management.utils.Constants.DELETED_STATUS;
/**
 * Service class for processing gateways.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GatewaysService {
    private final GatewaysRepository gatewaysRepo;
    private final GatewaysMapper gatewaysMapper;
    private final UtilsBase utilsBase;
    private final OperatorMnoRepository operatorRepo;
    private final SequenceNetworksIdGenerator seqGateway;
    private final AppProperties appProperties;
    /**
     * Retrieves the list of gateways
     *
     * @return A ResponseDTO containing the list of gateways.
     */
    public ApiResponse getGateways() {
        try {
            var result = gatewaysRepo.findByEnabledNot(DELETED_STATUS);
            return ResponseMapping.successMessage("Get gateways request success", gatewaysMapper.toDTO(result));
        } catch (Exception e) {
            log.error("Error to get gateways: {}", e.getMessage());
            return ResponseMapping.exceptionMessage("Gateways was end with error", e);
        }
    }

    /**
     * Creates a new gateway, notify by socket and update Redis.
     *
     * @param newGateways The new gateway to create.
     * @return A ResponseDTO indicating the success or failure of the operation.
     */
    public ApiResponse create(GatewaysDTO newGateways) {
        try {
            this.utilsBase.isRequestDlrAndTransmitterBind(newGateways.getBindType(), newGateways.isRequestDlr(), newGateways.getProtocol());
            this.utilsBase.validateMaxLengthSpAndGw(newGateways.getPassword(), newGateways.getSystemId(), newGateways.getProtocol());
            if (!existsSystemIdAndEnabled(newGateways.getSystemId(), DELETED_STATUS, 0, "create")) {
                var gatewayEntity = gatewaysMapper.toEntity(newGateways);
                gatewayEntity.setNetworkId(seqGateway.getNextNetworkIdSequenceValue("GW"));
                gatewayEntity.setStatus(Constants.BIND_DEFAULT_STATUS);
                gatewayEntity.setActiveSessionsNumbers(0);
                var resultEntity = gatewaysRepo.save(gatewayEntity);
                
                // create format to redis
                ParseGatewaysDTO redisGatewayDTO = this.createGatewayFormatToRedis(resultEntity.getNetworkId());
                
                String gatewayString = redisGatewayDTO.toString();
                Objects.requireNonNull(gatewayString, "gatewayString should not be null");
                utilsBase.storeInRedis(appProperties.getGatewaysKey(), newGateways.getSystemId(), gatewayString);
                utilsBase.sendNotificationSocket(utilsBase.findUpdateEndpointByProtocolToGw(redisGatewayDTO.getProtocol()), newGateways.getSystemId());


                log.info("New gateway: {}", gatewayString);
                return ResponseMapping.successMessage("Gateway added successful.", redisGatewayDTO);
            }

            return ResponseMapping.errorMessage("There is already an active system id, you must assign a different system id.");
        } catch (DataIntegrityViolationException e) {
            throw e;
        } catch (Exception e) {
            log.error("New gateways request with error: {}", e.getMessage());
            return ResponseMapping.exceptionMessage("New gateways request with error", e);
        }
    }

    /**
     * Updates an existing gateway, notify by socket and update Redis.
     *
     * @param id The ID of the gateway to update.
     * @param gateway The updated gateway information.
     * @return A ResponseDTO indicating the success or failure of the operation.
     */
    public ApiResponse update(int id, GatewaysDTO gateway) {
        try {
            this.utilsBase.isRequestDlrAndTransmitterBind(gateway.getBindType(), gateway.isRequestDlr(), gateway.getProtocol());
            this.utilsBase.validateMaxLengthSpAndGw(gateway.getPassword(), gateway.getSystemId(), gateway.getProtocol());
            if (!existsSystemIdAndEnabled(gateway.getSystemId(), DELETED_STATUS, id, "update")) {
                var findGatewayEntity = gatewaysRepo.findById(id);
                if (Objects.nonNull(findGatewayEntity)) {
                    if (findGatewayEntity.getEnabled() == DELETED_STATUS) {
                        return ResponseMapping.errorMessage("Illegal exception it is not possible to modify a deleted account.");
                    }
                    int enablePrevious = findGatewayEntity.getEnabled();
                    String currentSystemId = findGatewayEntity.getSystemId();
                    String currentProtocol = findGatewayEntity.getProtocol();
                    
                    gateway.setNetworkId(id);
                    gatewaysMapper.updateEntityFromDTO(gateway, findGatewayEntity);
                    Gateways resultEntity = gatewaysRepo.save(findGatewayEntity);
                    
                    // create format to Redis
                    ParseGatewaysDTO redisGatewayDTO = this.createGatewayFormatToRedis(resultEntity.getNetworkId());
                    utilsBase.storeInRedis(appProperties.getGatewaysKey(), resultEntity.getSystemId(), redisGatewayDTO.toString());

                    //remove objects from current system id if updated from GUI
                    if (!currentSystemId.equals(resultEntity.getSystemId())) {
                    	utilsBase.removeInRedis(appProperties.getGatewaysKey(), currentSystemId);
                    	utilsBase.sendNotificationSocket(utilsBase.findDeleteEndpointByProtocolToGw(currentProtocol), currentSystemId);
					}
                    this.manageSocketNotifications(gateway, redisGatewayDTO, enablePrevious);
                    return ResponseMapping.successMessage("Gateway updated successfully.", redisGatewayDTO);
                }

                return ResponseMapping.errorMessageNoFound("Gateway with network_id= " + id + " was not found.");
            }

            return ResponseMapping.errorMessage("There is already an active system id, you must assign a different system id.");
        } catch (DataIntegrityViolationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Update gateways request with error: {}", e.getMessage());
            return ResponseMapping.exceptionMessage("Update gateways request with error", e);
        }
    }

    /**
     * Finds if a system ID and enable status combination exists in the gateways.
     *
     * @param systemId  The system ID to search for.
     * @param enabled   The enabled status to search for.
     * @param networkId The network ID to exclude (for update operations).
     * @param type      The type of operation ('create' or 'update').
     * @return True if the combination exists, false otherwise.
     */
    public boolean existsSystemIdAndEnabled(String systemId, int enabled, int networkId, String type) {
        try {
            List<Gateways> gatewayFound = switch (type) {
                case "create" -> gatewaysRepo.findAllBySystemIdAndEnabledNot(systemId, enabled);
                case "update" -> gatewaysRepo.findBySystemIdAndEnabledNotAndNetworkIdNot(systemId, enabled, networkId);
                default -> throw new IllegalArgumentException("Unexpected value: " + type);
            };

            return gatewayFound != null && !gatewayFound.isEmpty();
        } catch (Exception e) {
            log.error("An error has occurred on existsSystemIdAndEnabled{}", e.getMessage());
        }

        return false;
    }

    private void manageSocketNotifications(GatewaysDTO gateway, ParseGatewaysDTO redisGatewayDTO, int enablePrevious) {
        if (gateway.getEnabled() != enablePrevious) {
            String endpoint = utilsBase.findEndpointToGateway(gateway.getProtocol(), gateway.getEnabled());
            switch (gateway.getEnabled()) {
                case 0, 2:
                    utilsBase.sendNotificationSocket(endpoint, gateway.getSystemId());
                    break;
                case 1:
                    log.info("Gateway with network_id= {} was connected via manageSocketNotifications.", gateway.getNetworkId());
                    utilsBase.sendNotificationSocket(endpoint, gateway.getSystemId());
                    break;
                default:
                    break;
            }
        } else {
            utilsBase.sendNotificationSocket(utilsBase.findUpdateEndpointByProtocolToGw(redisGatewayDTO.getProtocol()), gateway.getSystemId());
        }
    }


    /**
     * Creates a gateway format suitable for storing in Redis based on the network ID.
     *
     * @param networkId The network ID of the gateway.
     * @return A {@link ParseGatewaysDTO} representing the gateway format suitable for Redis, or null if an error occurs.
     */
    public ParseGatewaysDTO createGatewayFormatToRedis(int networkId) {
    	try {
    		// searching gateway by networkId
    		Gateways gatewayEntity = gatewaysRepo.findById(networkId);
    		ParseGatewaysDTO redisGatewayDTO = gatewaysMapper.toDTO(gatewayEntity);
    		
    		// adding message id fields from MNO
            OperatorMno mno = operatorRepo.findById(redisGatewayDTO.getMnoId());
            redisGatewayDTO.setTlvMessageReceiptId(mno.isTlvMessageReceiptId());
            redisGatewayDTO.setMessageIdDecimalFormat(mno.isMessageIdDecimalFormat());
            
            return redisGatewayDTO;
    		
		} catch (Exception e) {
			log.error("Error create gateway format to redis {}", e.getMessage());
			return null;
		}
    }
    
    /**
     * Loads gateway data for a specific network into Redis and sends a notification via socket.
     * loading and notification for gateways during the overall loading process.
     * @param networkId The ID of the network for which to load gateway data.
     */
    public void onlyToLoadInitInRedisAndSocket(int networkId) {
    	ParseGatewaysDTO redisGatewayDTO = this.createGatewayFormatToRedis(networkId);
        utilsBase.storeInRedis(appProperties.getGatewaysKey(), redisGatewayDTO.getSystemId(), redisGatewayDTO.toString());
        utilsBase.sendNotificationSocket(utilsBase.findUpdateEndpointByProtocolToGw(redisGatewayDTO.getProtocol()), redisGatewayDTO.getSystemId());
    }
    
    /**
     * Updates all gateways associated with the specified MNO (Mobile Network Operator) in Redis.
     *
     * @param mnoId the ID of the MNO
     * @return true if the update is successful, false otherwise
     */
    public boolean updateAllGatewayByMno(int mnoId) {
    	try {
			List<Gateways> gateways = gatewaysRepo.findByMnoIdAndEnabledNot(mnoId, DELETED_STATUS);
			for (Gateways gateway : gateways) {
				ParseGatewaysDTO redisGatewayDTO = this.createGatewayFormatToRedis(gateway.getNetworkId());
                utilsBase.storeInRedis(appProperties.getGatewaysKey(), gateway.getSystemId(), redisGatewayDTO.toString());
				utilsBase.sendNotificationSocket(utilsBase.findUpdateEndpointByProtocolToGw(gateway.getProtocol()), gateway.getSystemId());
			}
			return true;
		} catch (Exception e) {
			log.error("Error to update gateway with mnoId {} -> {}" , mnoId, e.getMessage());
			return false;
		}
    }
}
