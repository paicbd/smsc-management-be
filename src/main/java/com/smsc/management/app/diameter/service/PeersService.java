package com.smsc.management.app.diameter.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.smsc.management.utils.ApiResponse;
import com.smsc.management.app.diameter.dto.PeersDTO;
import com.smsc.management.app.diameter.model.entity.Peers;
import com.smsc.management.app.diameter.mapper.PeersMapper;
import com.smsc.management.app.diameter.model.repository.PeersRepository;
import com.smsc.management.utils.ResponseMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PeersService {
	private final PeersRepository peersRepo;
	private final PeersMapper peersMapper;
	private final ObjectDiameterService processObject;
	
	public ApiResponse getPeersConfig(int diameterId) {
        try {
        	List<Peers> result = peersRepo.findByDiameterId(diameterId);
        	if (result == null) {
        		return ResponseMapping.errorMessageNoFound("Peers was not found.");
			}
        	return ResponseMapping.successMessage("Get peers request success", peersMapper.toDTOList(result));
        } catch (Exception e) {
        	log.error("Get peers request with error: {}", e.getMessage());
            return ResponseMapping.exceptionMessage("Peers was end with error", e);
		}
	}

	public ApiResponse create(PeersDTO peer) {
		try {
			Peers peerEntity = peersMapper.toEntity(peer);
			this.createUri(peerEntity);
			var result = peersRepo.save(peerEntity);
			
			processObject.updateOrCreateJsonInRedis(result.getDiameterId(), true);
			
			return ResponseMapping.successMessage("new peer created successful.", peersMapper.toDTO(result));
		} catch (Exception e) {
			log.error("New peers request with error: {}", e.getMessage());
			return ResponseMapping.exceptionMessage("Peers was end with error", e);
		}
	}
	
	public ApiResponse update(int id, PeersDTO peer) {
		try {
			Peers currentPeer = peersRepo.findById(id);
			if (currentPeer == null) {
				return ResponseMapping.errorMessageNoFound("Peer with id= " + id + " was not found.");
			}
			peersMapper.updateEntityFromDTO(peer, currentPeer);
			this.createUri(currentPeer);
			var result = peersRepo.save(currentPeer);
			
			// send to redis
			processObject.updateOrCreateJsonInRedis(result.getDiameterId(), true);
						
			return ResponseMapping.successMessage("Peer updated successful.", peersMapper.toDTO(result));
		} catch (Exception e) {
			log.error("Update peer request with error: {}", e.getMessage());
			return ResponseMapping.exceptionMessage("Update peer was end with error", e);
		}
	}
	
	public ApiResponse delete(int id) {
        try {
        	Peers currentPeer = peersRepo.findById(id);
        	if (currentPeer == null) {
				return ResponseMapping.errorMessageNoFound("Peer with id= " + id + " was not found.");
			}
        	peersRepo.delete(currentPeer);
        	
        	// send to redis
			processObject.updateOrCreateJsonInRedis(currentPeer.getDiameterId(), true);
						
			return ResponseMapping.successMessage("Peer deleted successful.", peersMapper.toDTO(currentPeer));
        } catch (Exception e) {
        	log.error("Delete Peer request with error: {}", e.getMessage());
            return ResponseMapping.exceptionMessage("Delete Peer was end with error", e);
		}
	}
	
	private void createUri(Peers peer) {
		try {
			
			String port = peer.getPort() != null ? ":" + peer.getPort() : "";
			String uri = "aaa://" + peer.getIp() + port;
			
			if (peer.isUseUriAsFqdn()) {
				uri = "aaa://" + peer.getHost() + port;
			} 
			
			peer.setUri(uri);
		} catch (Exception e) {
			log.error("Error to create uri for the peer -> {}", peer.toString());
		}
	}
}
