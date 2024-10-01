package com.smsc.management.app.diameter.service;

import org.springframework.stereotype.Service;

import com.smsc.management.utils.ApiResponse;
import com.smsc.management.app.diameter.dto.LocalPeerDTO;
import com.smsc.management.app.diameter.model.entity.LocalPeer;
import com.smsc.management.app.diameter.mapper.LocalPeerMapper;
import com.smsc.management.app.diameter.model.repository.LocalPeerRepository;
import com.smsc.management.utils.ResponseMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocalPeerService {
	private final LocalPeerRepository localPeerRepo;
	private final LocalPeerMapper localPeerMapper;
	private final ObjectDiameterService processObject;
	
	public ApiResponse getLocalPeerConfig(int diameterId) {
        try {
        	LocalPeer result =  localPeerRepo.findByDiameterId(diameterId);
        	if (result == null) {
        		return ResponseMapping.errorMessageNoFound("Local peer config was not found.");
			}
        	return ResponseMapping.successMessage("Get local peer request success", localPeerMapper.toDTO(result));
        } catch (Exception e) {
        	log.error("Get Local peer request with error: {}", e.getMessage());
            return ResponseMapping.exceptionMessage("Local peer was end with error", e);
		}
	}
	
	public ApiResponse create(LocalPeerDTO localPeer) {
		try {
			LocalPeer currentLocalPeer = localPeerRepo.findByDiameterId(localPeer.getDiameterId());
			if (currentLocalPeer != null) {
				return ResponseMapping.errorMessage("Diameter configuration with id=" + localPeer.getDiameterId() + " already has a local_peer config set.");
			}
			LocalPeer newLocalPeer = localPeerMapper.toEntity(localPeer);
			var result = localPeerRepo.save(newLocalPeer);
			
			processObject.updateOrCreateJsonInRedis(result.getDiameterId(), true);
			
			return ResponseMapping.successMessage("New local peer added successful.", localPeerMapper.toDTO(result));
			
		} catch (Exception e) {
			log.error("New local peer request with error: {}", e.getMessage());
			return ResponseMapping.exceptionMessage("Local peer was end with error", e);
		}
	}
	
	public ApiResponse update(int id, LocalPeerDTO localPeer) {
		try {
			LocalPeer currentLocalpeer = localPeerRepo.findById(id);
			if (currentLocalpeer == null) {
				return ResponseMapping.errorMessageNoFound("Loca peer config with id= " + id + " was not found.");
			}
			
			localPeerMapper.updateEntityFromDTO(localPeer, currentLocalpeer);
			var result = localPeerRepo.save(currentLocalpeer);
			
			// send to redis
			processObject.updateOrCreateJsonInRedis(result.getDiameterId(), true);
						
			return ResponseMapping.successMessage("Local peer config updated successful.", localPeerMapper.toDTO(result));
			
		} catch (Exception e) {
			log.error("Update Local peer request with error: {}", e.getMessage());
			return ResponseMapping.exceptionMessage("Update Local peer was end with error", e);
		}
	}
}
