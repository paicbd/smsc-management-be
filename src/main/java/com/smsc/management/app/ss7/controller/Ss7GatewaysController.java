package com.smsc.management.app.ss7.controller;

import com.smsc.management.app.ss7.dto.TcapDTO;
import com.smsc.management.app.ss7.dto.MapDTO;
import com.smsc.management.app.ss7.dto.SccpAddressesDTO;
import com.smsc.management.app.ss7.service.MapService;
import com.smsc.management.app.ss7.service.TcapService;
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
import com.smsc.management.app.ss7.dto.M3uaApplicationServerDTO;
import com.smsc.management.app.ss7.dto.M3uaAssociationsDTO;
import com.smsc.management.app.ss7.dto.M3uaDTO;
import com.smsc.management.app.ss7.dto.M3uaRoutesDTO;
import com.smsc.management.app.ss7.dto.M3uaSocketsDTO;
import com.smsc.management.app.ss7.dto.SccpDTO;
import com.smsc.management.app.ss7.dto.SccpMtp3DestinationsDTO;
import com.smsc.management.app.ss7.dto.SccpRemoteResourcesDTO;
import com.smsc.management.app.ss7.dto.SccpRulesDTO;
import com.smsc.management.app.ss7.dto.SccpServiceAccessPointsDTO;
import com.smsc.management.app.ss7.dto.Ss7GatewaysDTO;
import com.smsc.management.app.ss7.service.SccpService;
import com.smsc.management.app.ss7.service.Ss7GatewaysService;
import com.smsc.management.app.ss7.service.ObjectSs7Service;
import com.smsc.management.app.ss7.service.M3uaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ss7-gateways")
public class Ss7GatewaysController {
	private final Ss7GatewaysService processGateways;
	private final M3uaService m3UaService;
	private final SccpService sccpService;
	private final TcapService tcapService;
	private final MapService mapService;
	private final ObjectSs7Service processObjectSss7;
	
	@GetMapping
	public ResponseEntity<ApiResponse> listGateway(){
		ApiResponse result = processGateways.getGateways();
		return ResponseEntity.status(result.status()).body(result);
	}
	
	@PostMapping("/create")
	public ResponseEntity<ApiResponse> create(@RequestBody @Valid Ss7GatewaysDTO gateway){
		ApiResponse result = processGateways.create(gateway);
		return ResponseEntity.status(result.status()).body(result);
	}
	
	@PutMapping("/update/{id}")
	public ResponseEntity<ApiResponse> update(@RequestBody @Valid Ss7GatewaysDTO gateway, @PathVariable int id){
		ApiResponse result = processGateways.update(id, gateway);
		return ResponseEntity.status(result.status()).body(result);
	}
	
	@GetMapping("/{networkId}")
	public ResponseEntity<ApiResponse> getGateway(@PathVariable int networkId){
		ApiResponse result = processGateways.getGatewaysByNetworkId(networkId);
		return ResponseEntity.status(result.status()).body(result);
	}
	
	@GetMapping("/refresh-setting/{networkId}")
	public ResponseEntity<ApiResponse> updateOrCreateInRedis(@PathVariable int networkId){
		ApiResponse result = processObjectSss7.refreshingSettingSs7Gateway(networkId);
		return ResponseEntity.status(result.status()).body(result);
	}
	
	/*
	 * ----------- M3UA -----------------------------
	 */
	@GetMapping("/m3ua/{networkId}")
	public ResponseEntity<ApiResponse> getM3ua(@PathVariable int networkId){
		ApiResponse result = m3UaService.getM3uaByNetworkId(networkId);
		return ResponseEntity.status(result.status()).body(result);
	}
	
	@PostMapping("/m3ua/create")
	public ResponseEntity<ApiResponse> createM3ua(@RequestBody @Valid M3uaDTO newM3ua){
		ApiResponse result = m3UaService.create(newM3ua);
		return ResponseEntity.status(result.status()).body(result);
	}
	
	@PutMapping("/m3ua/update/{id}")
	public ResponseEntity<ApiResponse> updateM3ua(@RequestBody @Valid M3uaDTO newM3ua, @PathVariable int id){
		ApiResponse result = m3UaService.update(id, newM3ua);
		return ResponseEntity.status(result.status()).body(result);
	}
	
	@DeleteMapping("/m3ua/delete/{id}")
	public ResponseEntity<ApiResponse> deleteM3ua(@PathVariable int id){
		ApiResponse result = m3UaService.delete(id);
		return ResponseEntity.status(result.status()).body(result);
	}
	
	/*
	 * ----------- M3UA SOCKETS -----------------------------
	 */
	@GetMapping("/m3ua/sockets/{m3uaId}")
	public ResponseEntity<ApiResponse> getM3uaServers(@PathVariable int m3uaId){
		ApiResponse result = m3UaService.getM3uaServers(m3uaId);
		return ResponseEntity.status(result.status()).body(result);
	}
	
	@PostMapping("/m3ua/sockets/create")
	public ResponseEntity<ApiResponse> createM3uaServer(@RequestBody @Valid M3uaSocketsDTO newM3uaServer){
		ApiResponse result = m3UaService.createM3uaServers(newM3uaServer);
		return ResponseEntity.status(result.status()).body(result);
	}
	
	@PutMapping("/m3ua/sockets/update/{id}")
	public ResponseEntity<ApiResponse> updateM3uaServer(@RequestBody @Valid M3uaSocketsDTO m3uaServer, @PathVariable int id){
		ApiResponse result = m3UaService.updateM3uaServers(id, m3uaServer);
		return ResponseEntity.status(result.status()).body(result);
	}
	
	@DeleteMapping("/m3ua/sockets/delete/{id}")
	public ResponseEntity<ApiResponse> deleteM3uaServer(@PathVariable int id){
		ApiResponse result = m3UaService.deleteM3uaServers(id);
		return ResponseEntity.status(result.status()).body(result);
	}
	
	/*
	 * ----------- M3UA ASSOCIATIONS -----------------------------
	 */
	@GetMapping("/m3ua/associations/{m3uaServerId}")
	public ResponseEntity<ApiResponse> getM3uaAssociation(@PathVariable int m3uaServerId){
		ApiResponse result = m3UaService.getM3uaAssociations(m3uaServerId);
		return ResponseEntity.status(result.status()).body(result);
	}
	
	@PostMapping("/m3ua/associations/create")
	public ResponseEntity<ApiResponse> createM3uaAssociation(@RequestBody @Valid M3uaAssociationsDTO newM3uaAssociation){
		ApiResponse result = m3UaService.createM3uaAssociations(newM3uaAssociation);
		return ResponseEntity.status(result.status()).body(result);
	}
	
	@PutMapping("/m3ua/associations/update/{id}")
	public ResponseEntity<ApiResponse> updateM3uaAssociation(@RequestBody @Valid M3uaAssociationsDTO m3uaAssociation, @PathVariable int id){
		ApiResponse result = m3UaService.updateM3uaAssociations(id, m3uaAssociation);
		return ResponseEntity.status(result.status()).body(result);
	}
	
	@DeleteMapping("/m3ua/associations/delete/{id}")
	public ResponseEntity<ApiResponse> deleteM3uaAssociation(@PathVariable int id){
		ApiResponse result = m3UaService.deleteM3uaAssociations(id);
		return ResponseEntity.status(result.status()).body(result);
	}

	/*
	 * ----------- M3UA APPLICATION SERVERS -----------------------------
	 */
	@GetMapping("/m3ua/application-server/{m3uaId}")
	public ResponseEntity<ApiResponse> getM3uaAppServer(@PathVariable int m3uaId){
		ApiResponse result = m3UaService.getM3uaAppServer(m3uaId);
		return ResponseEntity.status(result.status()).body(result);
	}
	
	@PostMapping("/m3ua/application-server/create")
	public ResponseEntity<ApiResponse> createM3uaAppServer(@RequestBody @Valid M3uaApplicationServerDTO newM3uaAppServer){
		ApiResponse result = m3UaService.createM3uaAppServer(newM3uaAppServer);
		return ResponseEntity.status(result.status()).body(result);
	}
	
	@PutMapping("/m3ua/application-server/update/{id}")
	public ResponseEntity<ApiResponse> updateM3uaAppServer(@RequestBody @Valid M3uaApplicationServerDTO m3uaAppServer, @PathVariable int id){
		ApiResponse result = m3UaService.updateM3uaAppServer(id, m3uaAppServer);
		return ResponseEntity.status(result.status()).body(result);
	}
	
	@DeleteMapping("/m3ua/application-server/delete/{id}")
	public ResponseEntity<ApiResponse> deleteM3uaAppServer(@PathVariable int id){
		ApiResponse result = m3UaService.deleteM3uaAppServer(id);
		return ResponseEntity.status(result.status()).body(result);
	}
	
	/*
	 * ----------- M3UA ROUTES -----------------------------
	 */
	@GetMapping("/m3ua/routes/{m3uaId}")
	public ResponseEntity<ApiResponse> getM3uaRoutes(@PathVariable int m3uaId){
		ApiResponse result = m3UaService.getM3uaRoutes(m3uaId);
		return ResponseEntity.status(result.status()).body(result);
	}
	
	@PostMapping("/m3ua/routes/create/{m3uaId}")
	public ResponseEntity<ApiResponse> createM3uaRoutes(@RequestBody @Valid M3uaRoutesDTO newM3uaRoutes, @PathVariable int m3uaId){
		ApiResponse result = m3UaService.createM3uaRoutes(m3uaId, newM3uaRoutes);
		return ResponseEntity.status(result.status()).body(result);
	}
	
	@PutMapping("/m3ua/routes/update/{id}")
	public ResponseEntity<ApiResponse> updateM3uaRoutes(@RequestBody @Valid M3uaRoutesDTO m3uaRoutes, @PathVariable int id){
		ApiResponse result = m3UaService.updateM3uaRoutes(id, m3uaRoutes);
		return ResponseEntity.status(result.status()).body(result);
	}
	
	@DeleteMapping("/m3ua/routes/delete/{id}")
	public ResponseEntity<ApiResponse> deleteM3uaRoutes(@PathVariable int id){
		ApiResponse result = m3UaService.deleteM3uaRoute(id);
		return ResponseEntity.status(result.status()).body(result);
	}
	
	/*
	 * ----------- SCCP -----------------------------
	 */
	@GetMapping("/sccp/{networkId}")
	public ResponseEntity<ApiResponse> getSccp(@PathVariable int networkId){
		ApiResponse result = sccpService.getSccpByNetworkId(networkId);
		return ResponseEntity.status(result.status()).body(result);
	}
	
	@PostMapping("/sccp/create")
	public ResponseEntity<ApiResponse> createSccp(@RequestBody @Valid SccpDTO newSccp){
		ApiResponse result = sccpService.create(newSccp);
		return ResponseEntity.status(result.status()).body(result);
	}
	
	@PutMapping("/sccp/update/{id}")
	public ResponseEntity<ApiResponse> updateSccp(@RequestBody @Valid SccpDTO sccp, @PathVariable int id){
		ApiResponse result = sccpService.update(id, sccp);
		return ResponseEntity.status(result.status()).body(result);
	}
	
	@DeleteMapping("/sccp/delete/{id}")
	public ResponseEntity<ApiResponse> deleteSccp(@PathVariable int id){
		ApiResponse result = sccpService.delete(id);
		return ResponseEntity.status(result.status()).body(result);
	}
	
	/*
	 * SCCP REMOTE RESOURCES
	 */
	@GetMapping("/sccp/remote-resource/{sccpId}")
	public ResponseEntity<ApiResponse> getSccpRemoteResource(@PathVariable int sccpId){
		ApiResponse result = sccpService.getSccpRemoteResources(sccpId);
		return ResponseEntity.status(result.status()).body(result);
	}
	
	@PostMapping("/sccp/remote-resource/create")
	public ResponseEntity<ApiResponse> createSccpRemoteResource(@RequestBody @Valid SccpRemoteResourcesDTO newSccpRemoteResource){
		ApiResponse result = sccpService.createSccpRemoteResources(newSccpRemoteResource);
		return ResponseEntity.status(result.status()).body(result);
	}
	
	@PutMapping("/sccp/remote-resource/update/{id}")
	public ResponseEntity<ApiResponse> updateSccpRemoteResource(@RequestBody @Valid SccpRemoteResourcesDTO sccpRemoteResource, @PathVariable int id){
		ApiResponse result = sccpService.updateSccpRemoteResources(id, sccpRemoteResource);
		return ResponseEntity.status(result.status()).body(result);
	}
	
	@DeleteMapping("/sccp/remote-resource/delete/{id}")
	public ResponseEntity<ApiResponse> deleteSccpRemoteResource(@PathVariable int id){
		ApiResponse result = sccpService.deleteSccpRemoteResources(id);
		return ResponseEntity.status(result.status()).body(result);
	}
	
	/*
	 * SCCP SERVICE ACCESS POINTS
	 */
	@GetMapping("/sccp/service-access-points/{sccpId}")
	public ResponseEntity<ApiResponse> getSccpServiceAccessPoint(@PathVariable int sccpId){
		ApiResponse result = sccpService.getSccpServiceAccessPoints(sccpId);
		return ResponseEntity.status(result.status()).body(result);
	}
	
	@PostMapping("/sccp/service-access-points/create")
	public ResponseEntity<ApiResponse> createSccpServiceAccessPoint(@RequestBody @Valid SccpServiceAccessPointsDTO newSccpSap){
		ApiResponse result = sccpService.createSccpServiceAccessPoints(newSccpSap);
		return ResponseEntity.status(result.status()).body(result);
	}
	
	@PutMapping("/sccp/service-access-points/update/{id}")
	public ResponseEntity<ApiResponse> updateSccpServiceAccessPoint(@RequestBody @Valid SccpServiceAccessPointsDTO sccpSap, @PathVariable int id){
		ApiResponse result = sccpService.updateSccpServiceAccessPoints(id, sccpSap);
		return ResponseEntity.status(result.status()).body(result);
	}
	
	@DeleteMapping("/sccp/service-access-points/delete/{id}")
	public ResponseEntity<ApiResponse> deleteSccpServiceAccessPoint(@PathVariable int id){
		ApiResponse result = sccpService.deleteSccpServiceAccessPoints(id);
		return ResponseEntity.status(result.status()).body(result);
	}
	
	/*
	 * SCCP MTP3 DESTINATIONS
	 */
	@GetMapping("/sccp/mtp3-destinations/{sccpId}")
	public ResponseEntity<ApiResponse> getSccpMtp3Destinations(@PathVariable int sccpId){
		ApiResponse result = sccpService.getSccpMtp3Destinations(sccpId);
		return ResponseEntity.status(result.status()).body(result);
	}
	
	@PostMapping("/sccp/mtp3-destinations/create")
	public ResponseEntity<ApiResponse> createSccpMtp3Destinations(@RequestBody @Valid SccpMtp3DestinationsDTO newSccpMtp3Dest){
		ApiResponse result = sccpService.createSccpMtp3Destinations(newSccpMtp3Dest);
		return ResponseEntity.status(result.status()).body(result);
	}
	
	@PutMapping("/sccp/mtp3-destinations/update/{id}")
	public ResponseEntity<ApiResponse> updateSccpMtp3Destinations(@RequestBody @Valid SccpMtp3DestinationsDTO sccpMtp3Dest, @PathVariable int id){
		ApiResponse result = sccpService.updateSccpMtp3Destinations(id, sccpMtp3Dest);
		return ResponseEntity.status(result.status()).body(result);
	}
	
	@DeleteMapping("/sccp/mtp3-destinations/delete/{id}")
	public ResponseEntity<ApiResponse> deleteSccpMtp3Destinations(@PathVariable int id){
		ApiResponse result = sccpService.deleteSccpMtp3Destinations(id);
		return ResponseEntity.status(result.status()).body(result);
	}

	/*
	 * ----------- TCAP ROUTES -----------------------------
	 */
	@GetMapping("/tcap/{networkId}")
	public ResponseEntity<ApiResponse> getTcap(@PathVariable int networkId){
		ApiResponse result = tcapService.getTcapByNetworkId(networkId);
		return ResponseEntity.status(result.status()).body(result);
	}

	@PostMapping("/tcap/create")
	public ResponseEntity<ApiResponse> createTcap(@RequestBody @Valid TcapDTO newTcap){
		ApiResponse result = tcapService.create(newTcap);
		return ResponseEntity.status(result.status()).body(result);
	}

	@PutMapping("/tcap/update/{id}")
	public ResponseEntity<ApiResponse> updateTcap(@RequestBody @Valid TcapDTO tcap, @PathVariable int id){
		ApiResponse result = tcapService.update(id, tcap);
		return ResponseEntity.status(result.status()).body(result);
	}

	@DeleteMapping("/tcap/delete/{id}")
	public ResponseEntity<ApiResponse> deleteTcap(@PathVariable int id){
		ApiResponse result = tcapService.delete(id);
		return ResponseEntity.status(result.status()).body(result);
	}
	/*
	 * ----------- MAP ROUTES -----------------------------
	 */
	@GetMapping("/map/{networkId}")
	public ResponseEntity<ApiResponse> getMap(@PathVariable int networkId){
		ApiResponse result = mapService.getMapByNetworkId(networkId);
		return ResponseEntity.status(result.status()).body(result);
	}

	@PostMapping("/map/create")
	public ResponseEntity<ApiResponse> createMap(@RequestBody @Valid MapDTO newMap){
		ApiResponse result = mapService.create(newMap);
		return ResponseEntity.status(result.status()).body(result);
	}

	@PutMapping("/map/update/{id}")
	public ResponseEntity<ApiResponse> updateMap(@RequestBody @Valid MapDTO map, @PathVariable int id){
		ApiResponse result = mapService.update(id, map);
		return ResponseEntity.status(result.status()).body(result);
	}

	@DeleteMapping("/map/delete/{id}")
	public ResponseEntity<ApiResponse> deleteMap(@PathVariable int id){
		ApiResponse result = mapService.delete(id);
		return ResponseEntity.status(result.status()).body(result);
	}
	
	/*
	 * ---------------- SCCP ADDRESS -------------
	 */
	@GetMapping("/sccp/address/{sccpId}")
	public ResponseEntity<ApiResponse> getSccpAddress(@PathVariable int sccpId){
		ApiResponse result = sccpService.getSccpAddress(sccpId);
		return ResponseEntity.status(result.status()).body(result);
	}
	
	@PostMapping("/sccp/address/create")
	public ResponseEntity<ApiResponse> createSccpAddress(@RequestBody @Valid SccpAddressesDTO newSccpAddress){
		ApiResponse result = sccpService.createSccpAddress(newSccpAddress);
		return ResponseEntity.status(result.status()).body(result);
	}
	
	@PutMapping("/sccp/address/update/{id}")
	public ResponseEntity<ApiResponse> updateSccpAddress(@RequestBody @Valid SccpAddressesDTO sccpAddress, @PathVariable int id){
		ApiResponse result = sccpService.updateSccpAddress(id, sccpAddress);
		return ResponseEntity.status(result.status()).body(result);
	}
	
	@DeleteMapping("/sccp/address/delete/{id}")
	public ResponseEntity<ApiResponse> deleteSccpAddress(@PathVariable int id){
		ApiResponse result = sccpService.deleteSccpAddress(id);
		return ResponseEntity.status(result.status()).body(result);
	}
	
	/*
	 * ---------------- SCCP RULES -------------
	 */
	@GetMapping("/sccp/rules/{sccpId}")
	public ResponseEntity<ApiResponse> getSccpRules(@PathVariable int sccpId){
		ApiResponse result = sccpService.getSccpRules(sccpId);
		return ResponseEntity.status(result.status()).body(result);
	}
	
	@PostMapping("/sccp/rules/create")
	public ResponseEntity<ApiResponse> createSccpRules(@RequestBody @Valid SccpRulesDTO newSccpRules){
		ApiResponse result = sccpService.createSccpRules(newSccpRules);
		return ResponseEntity.status(result.status()).body(result);
	}
	
	@PutMapping("/sccp/rules/update/{id}")
	public ResponseEntity<ApiResponse> updateSccpRules(@RequestBody @Valid SccpRulesDTO sccpAddress, @PathVariable int id){
		ApiResponse result = sccpService.updateSccpRules(id, sccpAddress);
		return ResponseEntity.status(result.status()).body(result);
	}
	
	@DeleteMapping("/sccp/rules/delete/{id}")
	public ResponseEntity<ApiResponse> deleteSccpRules(@PathVariable int id){
		ApiResponse result = sccpService.deleteSccpRules(id);
		return ResponseEntity.status(result.status()).body(result);
	}
}
