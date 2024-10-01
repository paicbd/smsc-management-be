package com.smsc.management.app.diameter.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.smsc.management.utils.ApiResponse;
import com.smsc.management.app.diameter.dto.DiameterDTO;
import com.smsc.management.app.diameter.model.entity.Diameter;
import com.smsc.management.app.diameter.mapper.DiameterMapper;
import com.smsc.management.app.diameter.model.repository.DiameterRepository;
import com.smsc.management.utils.Constants;
import com.smsc.management.utils.ResponseMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiameterService {
	private final DiameterRepository diameterRepo;
	private final DiameterMapper diameterMapper;
	private final ObjectDiameterService processObject;
	
	public ApiResponse getDiameterConfig() {
        try {
            List<Diameter> result = diameterRepo.findByEnabledNot(Constants.DELETED_STATUS);
            return ResponseMapping.successMessage("Get diameter init config request success", diameterMapper.toDTOList(result));
        } catch (Exception e) {
            log.error("Error to get diameter init config: {}", e.getMessage());
            return ResponseMapping.exceptionMessage("Error to get diameter init config", e);
        }
    }
	
	public ApiResponse create(DiameterDTO diameter) {
    	try {
    		Diameter currentDiameter = diameterRepo.findByEnabled(Constants.ENABLED_STATUS);
    		if (currentDiameter != null && diameter.getEnabled() == 1) {
    			log.warn("There is an active configuration, setting the enabled field value to disabled.");
    			diameter.setEnabled(Constants.DISABLED_STATUS);
    		}
    		
    		Diameter diameterEntity = diameterMapper.toEntity(diameter);
			var result = diameterRepo.save(diameterEntity);
			
			// send to redis
			processObject.updateOrCreateJsonInRedis(result.getId(), true);
			
			return ResponseMapping.successMessage("New diameter init config added successful.", diameterMapper.toDTO(result));
		} catch (Exception e) {
			log.error("Error to create diameter init config: {}", e.getMessage());
			return ResponseMapping.exceptionMessage("Error to create diameter init config", e);
		}
    }
	
	public ApiResponse update(int id, DiameterDTO diameter) {
    	try {
    		Diameter currentDiameter = diameterRepo.findById(id);
    		if (currentDiameter == null) {
    			return ResponseMapping.errorMessageNoFound("Diameter init config with id= " + id + " was not found.");
			}
    		
    		if (currentDiameter.getEnabled() == Constants.DELETED_STATUS) {
                return ResponseMapping.errorMessage("Illegal exception it is not possible to modify a deleted diameter.");
            }
    		
    		var enabledDiameter = diameterRepo.findByEnabled(Constants.ENABLED_STATUS);
    		if (currentDiameter.getEnabled() == Constants.DISABLED_STATUS && diameter.getEnabled() == Constants.ENABLED_STATUS
    				&& enabledDiameter != null) {
    			return ResponseMapping.errorMessage("Only one configuration for diameter is allowed to be active.");
			}
    		
    		boolean validStatus = true;
    		if (currentDiameter.getEnabled() == Constants.ENABLED_STATUS) {
    			validStatus = false;
			}
    		
    		diameterMapper.updateEntityFromDTO(diameter, currentDiameter);
			var result = diameterRepo.save(currentDiameter);
			
			// send to redis
			processObject.updateOrCreateJsonInRedis(result.getId(), validStatus);
						
			return ResponseMapping.successMessage("Diameter init config updated successful.", diameterMapper.toDTO(result));
		} catch (Exception e) {
			log.error("Error to update diameter init config: {}", e.getMessage());
			return ResponseMapping.exceptionMessage("Error to update diameter init config", e);
		}
    }
}
