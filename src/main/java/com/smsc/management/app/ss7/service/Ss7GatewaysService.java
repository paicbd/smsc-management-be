package com.smsc.management.app.ss7.service;

import static com.smsc.management.utils.Constants.DELETE_SS7_GATEWAY_ENDPOINT;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.smsc.management.utils.ApiResponse;
import com.smsc.management.app.ss7.dto.Ss7GatewaysDTO;
import com.smsc.management.app.ss7.model.entity.Ss7Gateways;
import com.smsc.management.app.ss7.mapper.Ss7GatewaysMapper;
import com.smsc.management.app.ss7.model.repository.Ss7GatewaysRepository;
import com.smsc.management.utils.Constants;
import com.smsc.management.utils.ResponseMapping;
import com.smsc.management.app.sequence.SequenceNetworksIdGenerator;
import com.smsc.management.utils.UtilsBase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class Ss7GatewaysService {
	private final Ss7GatewaysRepository ss7GatewayRepo;
	private final Ss7GatewaysMapper ss7GatewayMapper;
	private final SequenceNetworksIdGenerator seqGateway;
	private final UtilsBase utilsBase;
	
	@Value("${server.key.ss7GatewaysHash}")
    private String ss7GatewaysHash="ss7_gateways";
	
	@Value("${server.key.ss7SettingsHash}")
	private String ss7SettingsHash = "ss7_settings";
	
	/**
     * Retrieves the list of ss7 gateways
     *
     * @return A ResponseDTO containing the list of gateways.
     */
    public ApiResponse getGateways() {
        try {
            List<Ss7Gateways> result = ss7GatewayRepo.findByEnabledNot(Constants.DELETED_STATUS);
            return ResponseMapping.successMessage("Get Ss7 gateways request success", ss7GatewayMapper.toDTOList(result));
        } catch (Exception e) {
            log.error("Ss7 Gateways request with error on get : {}", e.getMessage());
            return ResponseMapping.exceptionMessage("Ss7 Gateways was end with error", e);
        }
    }
    
    /**
     * Creates a new ss7 gateway
     *
     * @param newSs7Gateways The new ss7 gateway to create.
     * @return A ResponseDTO indicating the success or failure of the operation.
     */
    public ApiResponse create(Ss7GatewaysDTO newSs7Gateways) {
    	try {
			Ss7Gateways ss7GatewayEntity = ss7GatewayMapper.toEntity(newSs7Gateways);
			ss7GatewayEntity.setNetworkId(seqGateway.getNextNetworkIdSequenceValue("GW"));
			ss7GatewayEntity.setStatus(Constants.BIND_STARTED_STATUS);
			ss7GatewayEntity.setEnabled(1);
			var result = ss7GatewayRepo.save(ss7GatewayEntity);
			
			return ResponseMapping.successMessage("New ss7 gateways added successful.", ss7GatewayMapper.toDTO(result));
		} catch (Exception e) {
			log.error("New ss7 gateways request with error: {}", e.getMessage());
			return ResponseMapping.exceptionMessage("Ss7 gateways was end with error", e);
		}
    }
    
    /**
     * Updates an existing ss7 gateway
     *
     * @param id The networkId of the gateway to update.
     * @param ss7Gateways The updated ss7 gateways information.
     * @return A ResponseDTO indicating the success or failure of the operation.
     */
    public ApiResponse update(int id, Ss7GatewaysDTO ss7Gateways) {
    	try {
			Ss7Gateways currentSs7Gateways = ss7GatewayRepo.findById(id);
			if (currentSs7Gateways != null) {
				if (currentSs7Gateways.getEnabled() == Constants.DELETED_STATUS) {
                    return ResponseMapping.errorMessage("Illegal exception it is not possible to modify a deleted ss7 gateways.");
                }
				ss7Gateways.setStatus(Constants.BIND_STARTED_STATUS);
				ss7GatewayMapper.updateEntityFromDTO(ss7Gateways, currentSs7Gateways);
				var result = ss7GatewayRepo.save(currentSs7Gateways);
				
				// socket and redis action only for deleting gateways
				if (result.getEnabled() == Constants.DELETED_STATUS) {
					utilsBase.removeInRedis(ss7GatewaysHash, Integer.toString(result.getNetworkId()));
	            	utilsBase.removeInRedis(ss7SettingsHash, Integer.toString(result.getNetworkId()));
	                utilsBase.sendNotificationSocket(DELETE_SS7_GATEWAY_ENDPOINT, Integer.toString(result.getNetworkId()));
				}
				
				return ResponseMapping.successMessage("ss7 gateways updated successful.", ss7GatewayMapper.toDTO(result));
			}
			return ResponseMapping.errorMessageNoFound("ss7 gateways with network_id= " + id + " was not found.");
		} catch (Exception e) {
			log.error("Update ss7 gateways request with error: {}", e.getMessage());
			return ResponseMapping.exceptionMessage("Ss7 gateways was end with error", e);
		}
    }
    
    public ApiResponse getGatewaysByNetworkId(int id) {
		try {
			Ss7Gateways result = ss7GatewayRepo.findById(id);
			if (result != null) {
				return ResponseMapping.successMessage("Get Ss7 gateways request success", ss7GatewayMapper.toDTO(result));
			}
			return ResponseMapping.errorMessageNoFound("ss7 gateways with network_id= " + id + " was not found.");
		} catch (Exception e) {
			log.error("Ss7 Gateways request with error on get by networkId: {}", e.getMessage());
			return ResponseMapping.exceptionMessage("Ss7 Gateways was end with error", e);
		}
	}

}
