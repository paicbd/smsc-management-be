package com.smsc.management.app.diameter.service;

import org.springframework.stereotype.Service;

import com.smsc.management.utils.ApiResponse;
import com.smsc.management.app.diameter.dto.ParametersDTO;
import com.smsc.management.app.diameter.model.entity.Parameters;
import com.smsc.management.app.diameter.mapper.ParametersMapper;
import com.smsc.management.app.diameter.model.repository.ParametersRepository;
import com.smsc.management.utils.ResponseMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParametersService {
	private final ParametersRepository parameterRepo;
	private final ParametersMapper parameterMapper;
	private final ObjectDiameterService processObject;

	public ApiResponse getParameter(int diameterId) {
        try {
        	Parameters result =  parameterRepo.findByDiameterId(diameterId);
        	if (result == null) {
        		return ResponseMapping.errorMessageNoFound("Parameters config was not found.");
			}
        	return ResponseMapping.successMessage("Get parameters config request success", parameterMapper.toDTO(result));
        } catch (Exception e) {
        	log.error("Get paramter config request with error: {}", e.getMessage());
            return ResponseMapping.exceptionMessage("Get Parameters config was end with error", e);
		}
	}
	
	public ApiResponse create(ParametersDTO parameter) {
        try {
        	Parameters currentParameters = parameterRepo.findByDiameterId(parameter.getDiameterId());
        	if (currentParameters != null) {
        		return ResponseMapping.errorMessage("Parameter configuration with id=" + parameter.getDiameterId() + " already has a local_peer config set.");
			}
        	
        	Parameters newLocalPeer = parameterMapper.toEntity(parameter);
			var result = parameterRepo.save(newLocalPeer);
			
			processObject.updateOrCreateJsonInRedis(result.getDiameterId(), true);
			
			return ResponseMapping.successMessage("New parameters added successful.", parameterMapper.toDTO(result));
        } catch (Exception e) {
        	log.error("New parameter request with error: {}", e.getMessage());
			return ResponseMapping.exceptionMessage("New parameters was end with error", e);
		}
	}
    
	public ApiResponse update(int id, ParametersDTO parameter) {
        try {
        	Parameters currentParameter = parameterRepo.findById(id);
        	if (currentParameter == null) {
        		return ResponseMapping.errorMessageNoFound("Parameter config with id= " + id + " was not found.");
			}
        	
        	parameterMapper.updateEntityFromDTO(parameter, currentParameter);
        	var result = parameterRepo.save(currentParameter);
        	
        	// send to redis
			processObject.updateOrCreateJsonInRedis(result.getDiameterId(), true);
						
			return ResponseMapping.successMessage("Parameters config updated successful.", parameterMapper.toDTO(result));
        	
        } catch (Exception e) {
        	log.error("Update parameters request with error: {}", e.getMessage());
			return ResponseMapping.exceptionMessage("Update parameters was end with error", e);
		}
	}
}
