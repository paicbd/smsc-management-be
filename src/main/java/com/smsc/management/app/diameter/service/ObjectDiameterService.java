package com.smsc.management.app.diameter.service;

import static com.smsc.management.utils.Constants.GENERAL_SETTINGS_DIAMETER_ENDPOINT;

import java.util.ArrayList;
import java.util.List;

import static com.smsc.management.utils.Constants.DELETE_GENERAL_SETTINGS_DIAMETER_ENDPOINT;
import static com.smsc.management.utils.Constants.ENABLED_STATUS;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.smsc.management.app.diameter.dto.LocalPeerDTO;
import com.smsc.management.app.diameter.dto.ParametersDTO;
import com.smsc.management.app.diameter.dto.PeersDTO;
import com.smsc.management.app.diameter.dto.RealmsDTO;
import com.smsc.management.app.diameter.dto.RedisDiameterDTO;
import com.smsc.management.app.diameter.model.entity.Applications;
import com.smsc.management.app.diameter.model.entity.Diameter;
import com.smsc.management.app.diameter.model.entity.LocalPeer;
import com.smsc.management.app.diameter.model.entity.Parameters;
import com.smsc.management.app.diameter.model.entity.Peers;
import com.smsc.management.app.diameter.mapper.LocalPeerMapper;
import com.smsc.management.app.diameter.mapper.ParametersMapper;
import com.smsc.management.app.diameter.mapper.PeersMapper;
import com.smsc.management.app.diameter.mapper.RealmsMapper;
import com.smsc.management.app.diameter.model.repository.ApplicationsRepository;
import com.smsc.management.app.diameter.model.repository.DiameterRepository;
import com.smsc.management.app.diameter.model.repository.LocalPeerRepository;
import com.smsc.management.app.diameter.model.repository.ParametersRepository;
import com.smsc.management.app.diameter.model.repository.PeersRepository;
import com.smsc.management.app.diameter.model.repository.RealmsRepository;
import com.smsc.management.utils.ResponseMapping;
import com.smsc.management.utils.UtilsBase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class ObjectDiameterService {
	private final DiameterRepository diameterRepo;
	private final LocalPeerRepository localPeersRepo;
	private final ParametersRepository parameterRepo;
	private final PeersRepository peersRepo;
	private final RealmsRepository realmsRepo;
	private final ApplicationsRepository applicationsRepo;
	
	private final LocalPeerMapper localPeerMapper;
	private final ParametersMapper parametersMapper;
	private final PeersMapper peersMapper;
	private final RealmsMapper realmsMapper;
	private final UtilsBase utilsBase;
	
	@Value("${general.settings}")
    private String generalSettingsHash="general_settings";
	
	@Value("${general.settings.key.diameter}")
    private String generalSettingsKey="diameter_config";
	
	public boolean updateOrCreateJsonInRedis(int id, boolean validStatus) throws Exception {
		try {
			Diameter diameterInit = diameterRepo.findById(id);
			if (diameterInit == null) {
				throw new Exception("Diameter configuration with id=" + id + " was not found.");
			}
			
			if (validStatus && diameterInit.getEnabled() != ENABLED_STATUS) {
				log.info("Diameter setting not enabled is not sent to Redis.");
				return false;
			}
			
			RedisDiameterDTO redisObject = new RedisDiameterDTO();
			RedisDiameterDTO.Network redisNetwork = redisObject.new Network();
			List<RedisDiameterDTO.RedisRealms> redisRealms = new ArrayList<>();
			
			// init config
			redisObject.setId(id);
			redisObject.setName(diameterInit.getName());
			redisObject.setEnabled(diameterInit.getEnabled());
			redisObject.setConnection(diameterInit.getConnection());
			
			// local peer
			LocalPeer localPeerEntity = localPeersRepo.findByDiameterId(id);
			if (localPeerEntity != null) {
				LocalPeerDTO localPeer = localPeerMapper.toDTO(localPeerEntity);
				localPeer.setDiameterId(null);
				redisObject.setLocalPeer(localPeer);
			}
			
			// parameters
			Parameters parameters = parameterRepo.findByDiameterId(id);
			if (parameters != null) {
				ParametersDTO parameterDto = parametersMapper.toDTO(parameters);
				parameterDto.setDiameterId(null);
				redisObject.setParameters(parameterDto);
			}
			
			// peers
			List<Peers> peers = peersRepo.findByDiameterId(id);
			if (peers != null) {
				List<PeersDTO> peersDto = peersMapper.toDTOList(peers);
				// removing diameter_id
				for (PeersDTO peer : peersDto) {
					peer.setDiameterId(null);
				}
				redisNetwork.setPeers(peersDto);
			}
			
			// realms
			List<RealmsDTO> realms = realmsRepo.fetchRealms(id);
			if (realms != null) {
				for (RealmsDTO realm : realms) {
					RedisDiameterDTO.RedisRealms redisRealm = redisObject.new RedisRealms();
					RedisDiameterDTO.RedisApplications redisApplication = redisObject.new RedisApplications();
					
					// realm
					redisRealm.setId(realm.getId());
					redisRealm.setName(realm.getName());
					redisRealm.setDomain(realm.getDomain());
					redisRealm.setLocalAction(realm.getLocalAction());
					redisRealm.setDynamic(realm.isDynamic());
					redisRealm.setExpTime(realm.getExpTime());
				
					// peer
					Peers peer = peersRepo.findById(realm.getPeerId());
					redisRealm.setPeers(peer.getIp());
					if (peer.isUseUriAsFqdn()) {
						redisRealm.setPeers(peer.getHost());
					}
					
					if (peer.getType().equalsIgnoreCase("PCRF")) {
						redisObject.setUsePcrf(true);
					} else {
						redisObject.setUseOcs(true);
					}
					
					// application
					Applications application = applicationsRepo.findById(realm.getApplicationId());
					redisApplication.setVendorId(application.getVendorId());
					redisApplication.setAuthApplId(application.getAuthApplId());
					redisApplication.setAcctApplId(application.getAcctApplId());
					
					redisRealm.setApplication(redisApplication);

					redisRealms.add(redisRealm);
				}
				
				redisNetwork.setRealms(redisRealms);
			}
			
			redisObject.setNetwork(redisNetwork);
			
			switch (diameterInit.getEnabled()) {
	        	case 0:
	        		log.info("Diameter configuration with id= {} was disabled -> {}", id, redisObject.toString());
	        		utilsBase.removeInRedis(generalSettingsHash, generalSettingsKey);
	                utilsBase.sendNotificationSocket(DELETE_GENERAL_SETTINGS_DIAMETER_ENDPOINT, generalSettingsKey);
	                break;
	            case 1:
	                log.info("Diameter configuration with id= {} was updated -> {}", id, redisObject.toString());
	                utilsBase.storeInRedis(generalSettingsHash, generalSettingsKey, redisObject.toString());
	                utilsBase.sendNotificationSocket(GENERAL_SETTINGS_DIAMETER_ENDPOINT, generalSettingsKey);
	                break;
	            case 2:
	            	utilsBase.removeInRedis(generalSettingsHash, generalSettingsKey);
	                utilsBase.sendNotificationSocket(DELETE_GENERAL_SETTINGS_DIAMETER_ENDPOINT, generalSettingsKey);
	                break;
	            default:
	                break;
			}
			
			return true;
			
		} catch (Exception e) {
			log.error("Error to crear JSON diameter -> {}", e.getMessage());
		}
		return false;
	}
}
