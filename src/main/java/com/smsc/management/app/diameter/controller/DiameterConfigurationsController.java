package com.smsc.management.app.diameter.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smsc.management.utils.ApiResponse;
import com.smsc.management.app.diameter.dto.DiameterDTO;
import com.smsc.management.app.diameter.dto.LocalPeerDTO;
import com.smsc.management.app.diameter.dto.ParametersDTO;
import com.smsc.management.app.diameter.dto.PeersDTO;
import com.smsc.management.app.diameter.dto.RealmsDTO;
import com.smsc.management.app.diameter.service.DiameterService;
import com.smsc.management.app.diameter.service.LocalPeerService;
import com.smsc.management.app.diameter.service.ParametersService;
import com.smsc.management.app.diameter.service.PeersService;
import com.smsc.management.app.diameter.service.RealmsService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/diameter-config")
public class DiameterConfigurationsController {
	private final DiameterService processDiameter;
	private final LocalPeerService processLocalPeer;
	private final ParametersService processParameters;
	private final PeersService processPeers;
	private final RealmsService processRealms;
	
	@GetMapping
	public ResponseEntity<ApiResponse> diameter(){
		ApiResponse result = processDiameter.getDiameterConfig();
		return ResponseEntity.status(result.status()).body(result);
	}
	
	@PostMapping("/create")
	public ResponseEntity<ApiResponse> create(@RequestBody @Valid DiameterDTO diameter){
		ApiResponse result = processDiameter.create(diameter);
		return ResponseEntity.status(result.status()).body(result);
	}
	
	@PutMapping("/update/{id}")
	public ResponseEntity<ApiResponse> update(@RequestBody @Valid DiameterDTO diameter, @PathVariable int id){
		ApiResponse result = processDiameter.update(id, diameter);
		return ResponseEntity.status(result.status()).body(result);
	}
	
	// local peer
	
	@GetMapping("/local-peer/{diameterId}")
	public ResponseEntity<ApiResponse> localPeer(@PathVariable int diameterId){
		ApiResponse result = processLocalPeer.getLocalPeerConfig(diameterId);
		return ResponseEntity.status(result.status()).body(result);
	}
	
	@PostMapping("/local-peer/create")
	public ResponseEntity<ApiResponse> createLocalPeer(@RequestBody @Valid LocalPeerDTO localPeer){
		ApiResponse result = processLocalPeer.create(localPeer);
		return ResponseEntity.status(result.status()).body(result);
	}
	
	@PutMapping("/local-peer/update/{id}")
	public ResponseEntity<ApiResponse> updateLocalPeer(@RequestBody @Valid LocalPeerDTO localPeer, @PathVariable int id){
		ApiResponse result = processLocalPeer.update(id, localPeer);
		return ResponseEntity.status(result.status()).body(result);
	}
	
	// parameters
	
	@GetMapping("/parameters/{diameterId}")
	public ResponseEntity<ApiResponse> parameters(@PathVariable int diameterId){
		ApiResponse result = processParameters.getParameter(diameterId);
		return ResponseEntity.status(result.status()).body(result);
	}
	
	@PostMapping("/parameters/create")
	public ResponseEntity<ApiResponse> createParameters(@RequestBody @Valid ParametersDTO parmaeters){
		ApiResponse result = processParameters.create(parmaeters);
		return ResponseEntity.status(result.status()).body(result);
	}
	
	@PutMapping("/parameters/update/{id}")
	public ResponseEntity<ApiResponse> updateParameters(@RequestBody @Valid ParametersDTO parmaeters, @PathVariable int id){
		ApiResponse result = processParameters.update(id, parmaeters);
		return ResponseEntity.status(result.status()).body(result);
	}
	
	// Peers
	
	@GetMapping("/peers/{diameterId}")
	public ResponseEntity<ApiResponse> peers(@PathVariable int diameterId){
		ApiResponse result = processPeers.getPeersConfig(diameterId);
		return ResponseEntity.status(result.status()).body(result);
	}
	
	@PostMapping("/peers/create")
	public ResponseEntity<ApiResponse> createPeers(@RequestBody @Valid PeersDTO peer){
		ApiResponse result = processPeers.create(peer);
		return ResponseEntity.status(result.status()).body(result);
	}
	
	@PutMapping("/peers/update/{id}")
	public ResponseEntity<ApiResponse> updatePeers(@RequestBody @Valid PeersDTO peer, @PathVariable int id){
		ApiResponse result = processPeers.update(id, peer);
		return ResponseEntity.status(result.status()).body(result);
	}
	
	@DeleteMapping("/peers/delete/{id}")
	public ResponseEntity<ApiResponse> deletePeers(@PathVariable int id){
		ApiResponse result = processPeers.delete(id);
		return ResponseEntity.status(result.status()).body(result);
	}
	
	// Realms
	
	@GetMapping("/realms/{diameterId}")
	public ResponseEntity<ApiResponse> realms(@PathVariable int diameterId){
		ApiResponse result = processRealms.getRealmsConfig(diameterId);
		return ResponseEntity.status(result.status()).body(result);
	}
	
	@PostMapping("/realms/create")
	public ResponseEntity<ApiResponse> createRealms(@RequestBody @Valid RealmsDTO realm){
		ApiResponse result = processRealms.create(realm);
		return ResponseEntity.status(result.status()).body(result);
	}
	
	@PutMapping("/realms/update/{id}")
	public ResponseEntity<ApiResponse> updateRealms(@RequestBody @Valid RealmsDTO realm, @PathVariable int id){
		ApiResponse result = processRealms.update(id, realm);
		return ResponseEntity.status(result.status()).body(result);
	}
	
	@DeleteMapping("/realms/delete/{id}")
	public ResponseEntity<ApiResponse> deleteRealm(@PathVariable int id){
		ApiResponse result = processRealms.delete(id);
		return ResponseEntity.status(result.status()).body(result);
	}
}
