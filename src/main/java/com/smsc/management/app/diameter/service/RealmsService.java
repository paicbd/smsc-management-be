package com.smsc.management.app.diameter.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.smsc.management.utils.ApiResponse;
import com.smsc.management.app.diameter.dto.RealmsDTO;
import com.smsc.management.app.diameter.model.entity.Peers;
import com.smsc.management.app.diameter.model.entity.Realms;
import com.smsc.management.app.diameter.mapper.RealmsMapper;
import com.smsc.management.app.diameter.model.repository.PeersRepository;
import com.smsc.management.app.diameter.model.repository.RealmsRepository;
import com.smsc.management.utils.ResponseMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RealmsService {
	private final RealmsRepository realmsRepo;
	private final PeersRepository peersRepo;
	private final RealmsMapper realmsMapper;
	private final ObjectDiameterService processObject;
	
	public ApiResponse getRealmsConfig(int diameterId) {
        try {
        	List<RealmsDTO> result = realmsRepo.fetchRealms(diameterId);
        	if (result == null) {
        		return ResponseMapping.errorMessageNoFound("Realms was not found.");
			}
        	return ResponseMapping.successMessage("Get realms request success", result);
        } catch (Exception e) {
        	log.error("Get realms request with error: {}", e.getMessage());
            return ResponseMapping.exceptionMessage("Realms was end with error", e);
		}
	}
	
	public ApiResponse create(RealmsDTO realm) {
        try {
        	Realms realmEntity = realmsMapper.toEntity(realm);
        	var result = realmsRepo.save(realmEntity);
        	
        	// find diameter id
        	Peers peer = peersRepo.findById(result.getPeerId());
        	processObject.updateOrCreateJsonInRedis(peer.getDiameterId(), true);
			
			return ResponseMapping.successMessage("new peer created successful.", realmsMapper.toDTO(result));
        } catch (Exception e) {
        	log.error("New Realm request with error: {}", e.getMessage());
            return ResponseMapping.exceptionMessage("New Realm was end with error", e);
		}
	}
	
	public ApiResponse update(int id, RealmsDTO realm) {
        try {
        	Realms currentRealm = realmsRepo.findById(id);
        	if (currentRealm == null) {
        		return ResponseMapping.errorMessageNoFound("Realm with id= " + id + " was not found.");
			}
        	realmsMapper.updateEntityFromDTO(realm, currentRealm);
        	var result = realmsRepo.save(currentRealm);
        	
        	// find diameter id
        	Peers peer = peersRepo.findById(result.getPeerId());
        	processObject.updateOrCreateJsonInRedis(peer.getDiameterId(), true);
			
			return ResponseMapping.successMessage("Realm updated successful.", realmsMapper.toDTO(result));
        } catch (Exception e) {
        	log.error("Update Realm request with error: {}", e.getMessage());
            return ResponseMapping.exceptionMessage("Update Realm was end with error", e);
		}
	}
	
	public ApiResponse delete(int id) {
        try {
        	Realms currentRealm = realmsRepo.findById(id);
        	if (currentRealm == null) {
        		return ResponseMapping.errorMessageNoFound("Realm with id= " + id + " was not found.");
			}	
        	realmsRepo.delete(currentRealm);
        	
        	// find diameter id
        	Peers peer = peersRepo.findById(currentRealm.getPeerId());
        	processObject.updateOrCreateJsonInRedis(peer.getDiameterId(), true);
			
			return ResponseMapping.successMessage("Realm deleted successful.", realmsMapper.toDTO(currentRealm));
        	
        } catch (Exception e) {
        	log.error("Delete Realm request with error: {}", e.getMessage());
            return ResponseMapping.exceptionMessage("Delete Realm was end with error", e);
		}
	}
}
