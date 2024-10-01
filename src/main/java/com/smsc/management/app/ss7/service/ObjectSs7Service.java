package com.smsc.management.app.ss7.service;

import static com.smsc.management.utils.Constants.DELETE_SS7_GATEWAY_ENDPOINT;
import static com.smsc.management.utils.Constants.UPDATE_SS7_GATEWAY_ENDPOINT;

import java.util.List;

import com.smsc.management.exception.SmscBackendException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.smsc.management.utils.ApiResponse;
import com.smsc.management.app.ss7.dto.M3uaApplicationServerDTO;
import com.smsc.management.app.ss7.dto.M3uaAssociationsDTO;
import com.smsc.management.app.ss7.dto.M3uaRoutesDTO;
import com.smsc.management.app.ss7.dto.RedisSs7DTO;
import com.smsc.management.app.ss7.dto.SccpMtp3DestinationsDTO;
import com.smsc.management.app.ss7.dto.SccpRulesDTO;
import com.smsc.management.app.ss7.dto.Ss7GatewaysDTO;
import com.smsc.management.app.ss7.model.entity.M3ua;
import com.smsc.management.app.ss7.model.entity.M3uaSockets;
import com.smsc.management.app.ss7.model.entity.Map;
import com.smsc.management.app.ss7.model.entity.Sccp;
import com.smsc.management.app.ss7.model.entity.SccpAddresses;
import com.smsc.management.app.ss7.model.entity.SccpRemoteResources;
import com.smsc.management.app.ss7.model.entity.SccpServiceAccessPoints;
import com.smsc.management.app.ss7.model.entity.Ss7Gateways;
import com.smsc.management.app.ss7.model.entity.Tcap;
import com.smsc.management.app.ss7.mapper.M3uaMapper;
import com.smsc.management.app.ss7.mapper.MapMapper;
import com.smsc.management.app.ss7.mapper.SccpMapper;
import com.smsc.management.app.ss7.mapper.Ss7GatewaysMapper;
import com.smsc.management.app.ss7.mapper.TcapMapper;
import com.smsc.management.app.ss7.model.repository.M3uaAppServersRouteRepository;
import com.smsc.management.app.ss7.model.repository.M3uaApplicationServerRepository;
import com.smsc.management.app.ss7.model.repository.M3uaAssAppServersRepository;
import com.smsc.management.app.ss7.model.repository.M3uaAssociationsRepository;
import com.smsc.management.app.ss7.model.repository.M3uaRepository;
import com.smsc.management.app.ss7.model.repository.M3uaRoutesRepository;
import com.smsc.management.app.ss7.model.repository.M3uaSocketsRepository;
import com.smsc.management.app.ss7.model.repository.MapRepository;
import com.smsc.management.app.ss7.model.repository.SccpAddressesRepository;
import com.smsc.management.app.ss7.model.repository.SccpMtp3DestinationsRepository;
import com.smsc.management.app.ss7.model.repository.SccpRemoteResourcesRepository;
import com.smsc.management.app.ss7.model.repository.SccpRepository;
import com.smsc.management.app.ss7.model.repository.SccpRulesRepository;
import com.smsc.management.app.ss7.model.repository.SccpServiceAccessPointsRepository;
import com.smsc.management.app.ss7.model.repository.Ss7GatewaysRepository;
import com.smsc.management.app.ss7.model.repository.TcapRepository;
import com.smsc.management.utils.ResponseMapping;
import com.smsc.management.utils.UtilsBase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ObjectSs7Service {
    /*
     * Gateways SS7
     */
    private final Ss7GatewaysRepository ss7GatewayRepo;
    private final Ss7GatewaysMapper ss7GatewayMapper;

    /*
     * M3UA
     */
    private final M3uaRepository m3uaRepo;
    private final M3uaSocketsRepository m3uaSocketRepo;
    private final M3uaAssociationsRepository m3uaAssociationsRepo;
    private final M3uaApplicationServerRepository m3uaAppServerRepo;
    private final M3uaAssAppServersRepository m3uaAssAppServerRepo;
    private final M3uaRoutesRepository m3uaRouteRepo;
    private final M3uaAppServersRouteRepository m3uaAppServerRouteRepo;
    private final M3uaMapper m3uaMapper;

    /*
     * SCCP
     */
    private final SccpRepository sccpRepo;
    private final SccpRemoteResourcesRepository sccpRemoteResourcesRepo;
    private final SccpServiceAccessPointsRepository sccpSapRepo;
    private final SccpMtp3DestinationsRepository sccpMtp3DestRepo;
    private final SccpAddressesRepository sccpAddressRepo;
    private final SccpRulesRepository sccpRulesRepo;
    private final SccpMapper sccpMapper;

    /*
     * MAP
     */
    private final MapRepository mapRepository;
    private final MapMapper mapMapper;

    /*
     * TCAP
     */
    private final TcapRepository tcapRepository;
    private final TcapMapper tcapMapper;

    private final UtilsBase utilsBase;

    @Value("${server.key.ss7GatewaysHash}")
    private String ss7GatewaysHash = "ss7_gateways";

    @Value("${server.key.ss7SettingsHash}")
    private String ss7SettingsHash = "ss7_settings";

    public ApiResponse refreshingSettingSs7Gateway(int networkId) {
        try {
            this.updateOrCreateJsonInRedis(networkId);
            return ResponseMapping.successMessage("success", null);
        } catch (Exception e) {
            log.error("Error to create JSON SS7 -> {}", e.getMessage());
            return ResponseMapping.exceptionMessage("Error to create JSON SS7", e);
        }
    }

    public void updateOrCreateJsonInRedis(int networkId) throws Exception {
        Ss7Gateways gateway = ss7GatewayRepo.findByNetworkId(networkId);
        validateObjectValue(gateway, "ss7 gateway");

        M3ua m3ua = m3uaRepo.findByNetworkId(networkId); // general
        validateObjectValue(m3ua, "m3ua");
        int m3uaId = m3ua.getId();

        List<M3uaSockets> m3uaSocket = m3uaSocketRepo.findBySs7M3uaId(m3uaId); // socket
        m3uaSocket = validateList(m3uaSocket);
        validateObjectValue(m3uaSocket, "m3ua sockets");

        List<M3uaAssociationsDTO> m3uaAssociations = m3uaAssociationsRepo.fetchM3uaAssociations(m3uaId); // associations
        m3uaAssociations = validateList(m3uaAssociations);
        validateObjectValue(m3uaAssociations, "m3ua associations");

        List<M3uaApplicationServerDTO> m3uaAppServers = m3uaAppServerRepo.fetchM3uaAppServer(m3uaId); // application servers
        m3uaAppServers = validateList(m3uaAppServers);
        validateObjectValue(m3uaAppServers, "m3ua application servers");
        for (M3uaApplicationServerDTO m3uaAppServer : m3uaAppServers) {
            List<Integer> aspFactories = m3uaAssAppServerRepo.fetchAssAppServers(m3uaAppServer.getId());
            m3uaAppServer.setAspFactories(aspFactories);
        }

        List<Integer> m3uaAppServersId = m3uaAppServerRepo.fetchM3uaAppServerId(m3uaId); // route
        List<M3uaRoutesDTO> m3uaRoutes = m3uaRouteRepo.fetchM3uaRoutes(m3uaAppServersId);
        m3uaRoutes = validateList(m3uaRoutes);
        validateObjectValue(m3uaRoutes, "m3ua routes");
        for (M3uaRoutesDTO m3uaRoute : m3uaRoutes) {
            List<Integer> appServers = m3uaAppServerRouteRepo.fetchAppServersRoute(m3uaRoute.getId());
            m3uaRoute.setAppServers(appServers);
        }

        Sccp sccp = sccpRepo.findByNetworkId(networkId); // general
        validateObjectValue(sccp, "sccp");
        int sccpId = sccp.getId();

        List<SccpRemoteResources> sccpRemoteResources = sccpRemoteResourcesRepo.findBySs7SccpId(sccpId); // remote resources
        sccpRemoteResources = validateList(sccpRemoteResources);
        validateObjectValue(sccpRemoteResources, "sccp remote resources");

        List<SccpServiceAccessPoints> sccpSap = sccpSapRepo.findBySs7SccpId(sccpId); // service access
        sccpSap = validateList(sccpSap);
        validateObjectValue(sccpSap, "sccp service access point");

        List<SccpMtp3DestinationsDTO> mtp3Destinations = sccpMtp3DestRepo.fetchMtp3Destinations(sccpId); // mtp3 destinations
        mtp3Destinations = validateList(mtp3Destinations);
        validateObjectValue(mtp3Destinations, "sccp mtp3 destinations");

        List<SccpAddresses> address = sccpAddressRepo.findBySs7SccpId(sccpId); // address
        address = validateList(address);
        validateObjectValue(address, "sccp address");

        List<SccpRulesDTO> rules = sccpRulesRepo.fetchSccpRules(sccpId); // rules
        rules = validateList(rules);
        validateObjectValue(rules, "sccp rules");

        Map map = mapRepository.findByNetworkId(networkId); // map
        validateObjectValue(map, "map");

        Tcap tcap = tcapRepository.findByNetworkId(networkId); // tcap
        validateObjectValue(tcap, "tcap");

        // convert to DTO
        RedisSs7DTO redisSs7DTO = new RedisSs7DTO();
        RedisSs7DTO.RedisM3ua objectM3ua = redisSs7DTO.new RedisM3ua();
        RedisSs7DTO.Redisassociations redisAssociations = redisSs7DTO.new Redisassociations();
        RedisSs7DTO.RedisSccp redisSccp = redisSs7DTO.new RedisSccp();
        RedisSs7DTO.RedisSap redisSap = redisSs7DTO.new RedisSap();

        // gateways
        Ss7GatewaysDTO redisSs7Settings = ss7GatewayMapper.toDTO(gateway);
        redisSs7DTO.setName(gateway.getName());
        redisSs7DTO.setNetworkId(gateway.getNetworkId());
        redisSs7DTO.setMnoId(gateway.getMnoId());
        // M3UA DTO
        objectM3ua.setGeneral(m3uaMapper.toDTO(m3ua));
        redisAssociations.setAssociations(m3uaAssociations);
        redisAssociations.setSockets(m3uaMapper.toDTOServerList(m3uaSocket));
        objectM3ua.setAssociations(redisAssociations);
        objectM3ua.setApplicationServers(m3uaAppServers);
        objectM3ua.setRoutes(m3uaRoutes);
        redisSs7DTO.setM3ua(objectM3ua);

        // sccp
        redisSccp.setGeneral(sccpMapper.toDTO(sccp));
        redisSccp.setRemoteResources(sccpMapper.toDTORemoteResources(sccpRemoteResources));
        redisSap.setServiceAccess(sccpMapper.toDTOSap(sccpSap));
        redisSap.setMtp3Destinations(mtp3Destinations);
        redisSccp.setServiceAccessPoints(redisSap);
        redisSccp.setAddresses(sccpMapper.toDTOAddress(address));
        redisSccp.setRules(rules);
        redisSs7DTO.setSccp(redisSccp);

        // tcap and map
        redisSs7DTO.setTcap(tcapMapper.toDTO(tcap));
        redisSs7DTO.setMap(mapMapper.toDTO(map));

        switch (gateway.getEnabled()) {
            case 0, 1:
                log.info("Ss7 Gateway with network_id= {} was connected -> {}", networkId, redisSs7DTO);
                utilsBase.storeInRedis(ss7GatewaysHash, Integer.toString(redisSs7DTO.getNetworkId()), redisSs7DTO.toString());
                utilsBase.storeInRedis(ss7SettingsHash, Integer.toString(redisSs7DTO.getNetworkId()), redisSs7Settings.toString());
                utilsBase.sendNotificationSocket(UPDATE_SS7_GATEWAY_ENDPOINT, Integer.toString(redisSs7DTO.getNetworkId()));
                break;
            case 2:
                utilsBase.removeInRedis(ss7GatewaysHash, Integer.toString(redisSs7DTO.getNetworkId()));
                utilsBase.removeInRedis(ss7SettingsHash, Integer.toString(redisSs7DTO.getNetworkId()));
                utilsBase.sendNotificationSocket(DELETE_SS7_GATEWAY_ENDPOINT, Integer.toString(redisSs7DTO.getNetworkId()));
                break;
            default:
                break;
        }
    }

    public void validateObjectValue(Object data, String objectName) {
        if (data == null || isListEmpty(data)) {
            throw new SmscBackendException("Object " + objectName + " cannot be null");
        }
    }

    /**
     * Checks if the given object is a list and if it is empty.
     *
     * @param obj The object to check.
     * @return true if the object is a list and is empty; false otherwise.
     */
    public static boolean isListEmpty(Object obj) {
        if (obj instanceof List<?> list) {
            return list.isEmpty();
        }
        return false;
    }

    /**
     * Checks if the list is null or empty. If so, returns null; otherwise, returns the list.
     *
     * @param list The list to check.
     * @param <T>  The type of elements in the list.
     * @return empty list if the list is null or empty; otherwise, returns the list.
     */
    private static <T> List<T> validateList(List<T> list) {
        if (list == null || list.isEmpty()) {
            return List.of();
        }
        return list;
    }
}
