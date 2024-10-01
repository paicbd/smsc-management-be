package com.smsc.management.app.ss7.service;

import com.smsc.management.app.ss7.controller.Ss7GatewaysController;
import com.smsc.management.app.ss7.dto.M3uaApplicationServerDTO;
import com.smsc.management.app.ss7.dto.M3uaAssociationsDTO;
import com.smsc.management.app.ss7.dto.M3uaDTO;
import com.smsc.management.app.ss7.dto.M3uaRoutesDTO;
import com.smsc.management.app.ss7.dto.M3uaSocketsDTO;
import com.smsc.management.app.ss7.dto.MapDTO;
import com.smsc.management.app.ss7.dto.SccpAddressesDTO;
import com.smsc.management.app.ss7.dto.SccpDTO;
import com.smsc.management.app.ss7.dto.SccpMtp3DestinationsDTO;
import com.smsc.management.app.ss7.dto.SccpRemoteResourcesDTO;
import com.smsc.management.app.ss7.dto.SccpRulesDTO;
import com.smsc.management.app.ss7.dto.SccpServiceAccessPointsDTO;
import com.smsc.management.app.ss7.dto.Ss7GatewaysDTO;
import com.smsc.management.app.ss7.dto.TcapDTO;
import com.smsc.management.app.ss7.mapper.M3uaMapper;
import com.smsc.management.app.ss7.mapper.MapMapper;
import com.smsc.management.app.ss7.mapper.SccpMapper;
import com.smsc.management.app.ss7.mapper.Ss7GatewaysMapper;
import com.smsc.management.app.ss7.mapper.TcapMapper;
import com.smsc.management.app.ss7.model.entity.M3ua;
import com.smsc.management.app.ss7.model.entity.M3uaSockets;
import com.smsc.management.app.ss7.model.entity.Map;
import com.smsc.management.app.ss7.model.entity.Sccp;
import com.smsc.management.app.ss7.model.entity.SccpAddresses;
import com.smsc.management.app.ss7.model.entity.SccpRemoteResources;
import com.smsc.management.app.ss7.model.entity.SccpServiceAccessPoints;
import com.smsc.management.app.ss7.model.entity.Ss7Gateways;
import com.smsc.management.app.ss7.model.entity.Tcap;
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
import com.smsc.management.app.ss7.utilsTest.Utils;
import com.smsc.management.integration.BaseIntegrationTest;
import com.smsc.management.utils.ApiResponse;
import com.smsc.management.utils.UtilsBase;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static com.smsc.management.app.ss7.utilsTest.Utils.checkAssertions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;


class ObjectSs7ServiceTest extends BaseIntegrationTest {
    @Autowired
    private Ss7GatewaysController ss7GatewaysController;

    @Autowired
    private ObjectSs7Service objectSs7Service;

    /*
     * Gateways SS7
     */
    @Autowired
    private Ss7GatewaysMapper ss7GatewaysMapper;
    @MockBean
    private Ss7GatewaysRepository ss7GatewaysRepository;

    /*
     * M3UA
     */
    @MockBean
    private M3uaRepository m3uaRepo;
    @MockBean
    private M3uaSocketsRepository m3uaSocketRepo;
    @MockBean
    private M3uaAssociationsRepository m3uaAssociationsRepo;
    @MockBean
    private M3uaApplicationServerRepository m3uaAppServerRepo;
    @MockBean
    private M3uaAssAppServersRepository m3uaAssAppServerRepo;
    @MockBean
    private M3uaRoutesRepository m3uaRouteRepo;
    @MockBean
    private M3uaAppServersRouteRepository m3uaAppServerRouteRepo;
    @Autowired
    private M3uaMapper m3uaMapper;

    /*
     * SCCP
     */
    @MockBean
    private SccpRepository sccpRepo;
    @MockBean
    private SccpRemoteResourcesRepository sccpRemoteResourcesRepo;
    @MockBean
    private SccpServiceAccessPointsRepository sccpSapRepo;
    @MockBean
    private SccpMtp3DestinationsRepository sccpMtp3DestRepo;
    @MockBean
    private SccpAddressesRepository sccpAddressRepo;
    @MockBean
    private SccpRulesRepository sccpRulesRepo;
    @Autowired
    private SccpMapper sccpMapper;

    /*
     * MAP
     */
    @MockBean
    private MapRepository mapRepository;
    @Autowired
    private MapMapper mapMapper;

    /*
     * TCAP
     */
    @MockBean
    private TcapRepository tcapRepository;
    @Autowired
    private TcapMapper tcapMapper;

    @MockBean
    private UtilsBase utilsBase;

    @Test
    void sendToRedisSS7GatewayTest() {
        Ss7GatewaysDTO gatewayDTOMock = Utils.getSs7GatewaysDTO();
        Ss7Gateways gatewaysEntity = ss7GatewaysMapper.toEntity(gatewayDTOMock);

        M3uaDTO m3uaDTOMock = Utils.getM3uatDtoMock();
        M3ua m3uaEntity = m3uaMapper.toEntity(m3uaDTOMock);
        M3uaSocketsDTO m3uaSocketsDTOMock = Utils.getM3uaSocketsMock();
        M3uaSockets m3uaSocketsEntity = m3uaMapper.toEntityServer(m3uaSocketsDTOMock);
        M3uaAssociationsDTO m3uaAssociationsDTOMock = Utils.getM3uaAssociationsDTO();
        M3uaApplicationServerDTO m3uaApplicationServerDTOMock = Utils.getM3uaApplicationServer();
        M3uaRoutesDTO m3uaRoutesDTOMock = Utils.getM3uaRoutesDTO();

        SccpDTO sccpDTOMock = Utils.getSccpDTOMock();
        Sccp sccpEntity = sccpMapper.toEntity(sccpDTOMock);
        SccpRemoteResourcesDTO sccpRemoteResourcesDTOMock = Utils.getSccpRemoteResourcesDTOMock();
        SccpRemoteResources sccpRemoteResourcesEntity = sccpMapper.toEntityRemoteResources(sccpRemoteResourcesDTOMock);
        SccpServiceAccessPointsDTO sccpServiceAccessPointsDTOMock = Utils.getSccpServiceAccessPointsDTOMock();
        SccpServiceAccessPoints sccpServiceAccessPointsEntity = sccpMapper.toEntitySap(sccpServiceAccessPointsDTOMock);
        SccpMtp3DestinationsDTO sccpMtp3DestinationsDTOMock = Utils.getSccpMtp3DestinationsDTOMock();
        SccpAddressesDTO sccpAddressesDTOMock = Utils.getSccpAddressesDTOMock();
        SccpAddresses sccpAddressesEntity = sccpMapper.toEntityAddress(sccpAddressesDTOMock);
        SccpRulesDTO sccpRulesDTO = Utils.getSccpRulesDTOMock();

        MapDTO mapDTOMock = Utils.getMapDTOMock();
        Map mapEntity = mapMapper.toEntity(mapDTOMock);

        TcapDTO tcapDTOMock = Utils.getTcapDTOMock();
        Tcap tcapEntity = tcapMapper.toEntity(tcapDTOMock);

        Mockito.when(ss7GatewaysRepository.findByNetworkId(anyInt())).thenReturn(gatewaysEntity);

        Mockito.when(m3uaRepo.findByNetworkId(anyInt())).thenReturn(m3uaEntity);
        Mockito.when(m3uaSocketRepo.findBySs7M3uaId(anyInt())).thenReturn(List.of(m3uaSocketsEntity));
        Mockito.when(m3uaAssociationsRepo.fetchM3uaAssociations(anyInt())).thenReturn(List.of(m3uaAssociationsDTOMock));
        Mockito.when(m3uaAppServerRepo.fetchM3uaAppServer(anyInt())).thenReturn(List.of(m3uaApplicationServerDTOMock));
        Mockito.when(m3uaAssAppServerRepo.fetchAssAppServers(anyInt())).thenReturn(List.of(1));
        Mockito.when(m3uaAppServerRepo.fetchM3uaAppServerId(anyInt())).thenReturn(List.of(1));
        Mockito.when(m3uaRouteRepo.fetchM3uaRoutes(anyList())).thenReturn(List.of(m3uaRoutesDTOMock));
        Mockito.when(m3uaAppServerRouteRepo.fetchAppServersRoute(anyInt())).thenReturn(List.of(1));

        Mockito.when(sccpRepo.findByNetworkId(anyInt())).thenReturn(sccpEntity);
        Mockito.when(sccpRemoteResourcesRepo.findBySs7SccpId(anyInt())).thenReturn(List.of(sccpRemoteResourcesEntity));
        Mockito.when(sccpSapRepo.findBySs7SccpId(anyInt())).thenReturn(List.of(sccpServiceAccessPointsEntity));
        Mockito.when(sccpMtp3DestRepo.fetchMtp3Destinations(anyInt())).thenReturn(List.of(sccpMtp3DestinationsDTOMock));
        Mockito.when(sccpAddressRepo.findBySs7SccpId(anyInt())).thenReturn(List.of(sccpAddressesEntity));
        Mockito.when(sccpRulesRepo.fetchSccpRules(anyInt())).thenReturn(List.of(sccpRulesDTO));

        Mockito.when(mapRepository.findByNetworkId(anyInt())).thenReturn(mapEntity);
        Mockito.when(tcapRepository.findByNetworkId(anyInt())).thenReturn(tcapEntity);

        Mockito.doNothing().when(utilsBase).storeInRedis(anyString(), anyString(), anyString());
        Mockito.doNothing().when(utilsBase).sendNotificationSocket(anyString(), anyString());

        ResponseEntity<ApiResponse> responseController = ss7GatewaysController.updateOrCreateInRedis(1);
        checkAssertions(responseController, HttpStatus.OK, "SS7REFRESH");

        gatewayDTOMock = Utils.getSs7GatewaysDTO();
        gatewayDTOMock.setEnabled(1);
        gatewaysEntity = ss7GatewaysMapper.toEntity(gatewayDTOMock);
        Mockito.when(ss7GatewaysRepository.findByNetworkId(anyInt())).thenReturn(gatewaysEntity);

        ApiResponse response = objectSs7Service.refreshingSettingSs7Gateway(1);
        checkOkAssertions(response);

        gatewayDTOMock = Utils.getSs7GatewaysDTO();
        gatewayDTOMock.setEnabled(2);
        gatewaysEntity = ss7GatewaysMapper.toEntity(gatewayDTOMock);
        Mockito.when(ss7GatewaysRepository.findByNetworkId(anyInt())).thenReturn(gatewaysEntity);

        response = objectSs7Service.refreshingSettingSs7Gateway(1);
        checkOkAssertions(response);

        gatewayDTOMock = Utils.getSs7GatewaysDTO();
        gatewayDTOMock.setEnabled(3);
        gatewaysEntity = ss7GatewaysMapper.toEntity(gatewayDTOMock);
        Mockito.when(ss7GatewaysRepository.findByNetworkId(anyInt())).thenReturn(gatewaysEntity);

        response = objectSs7Service.refreshingSettingSs7Gateway(1);
        checkOkAssertions(response);

        Mockito.when(ss7GatewaysRepository.findByNetworkId(anyInt())).thenReturn(null);
        response = objectSs7Service.refreshingSettingSs7Gateway(1);
        checkErrorAssertions(response);

        Mockito.when(ss7GatewaysRepository.findByNetworkId(anyInt())).thenReturn(gatewaysEntity);
        Mockito.when(m3uaSocketRepo.findBySs7M3uaId(anyInt())).thenReturn(null);
        response = objectSs7Service.refreshingSettingSs7Gateway(1);
        checkErrorAssertions(response);

        Mockito.when(m3uaSocketRepo.findBySs7M3uaId(anyInt())).thenReturn(List.of());
        response = objectSs7Service.refreshingSettingSs7Gateway(1);
        checkErrorAssertions(response);

        Mockito.when(m3uaSocketRepo.findBySs7M3uaId(anyInt())).thenReturn(List.of(m3uaSocketsEntity));
        Mockito.when(m3uaAssociationsRepo.fetchM3uaAssociations(anyInt())).thenReturn(List.of());
        response = objectSs7Service.refreshingSettingSs7Gateway(1);
        checkErrorAssertions(response);

        Mockito.when(m3uaAssociationsRepo.fetchM3uaAssociations(anyInt())).thenReturn(List.of(m3uaAssociationsDTOMock));
        Mockito.when(m3uaAppServerRepo.fetchM3uaAppServer(anyInt())).thenReturn(List.of());
        response = objectSs7Service.refreshingSettingSs7Gateway(1);
        checkErrorAssertions(response);

        Mockito.when(m3uaAppServerRepo.fetchM3uaAppServer(anyInt())).thenReturn(List.of(m3uaApplicationServerDTOMock));
        Mockito.when(m3uaRouteRepo.fetchM3uaRoutes(anyList())).thenReturn(List.of());
        response = objectSs7Service.refreshingSettingSs7Gateway(1);
        checkErrorAssertions(response);

        Mockito.when(m3uaRouteRepo.fetchM3uaRoutes(anyList())).thenReturn(List.of(m3uaRoutesDTOMock));
        Mockito.when(sccpRemoteResourcesRepo.findBySs7SccpId(anyInt())).thenReturn(List.of());
        response = objectSs7Service.refreshingSettingSs7Gateway(1);
        checkErrorAssertions(response);

        Mockito.when(sccpRemoteResourcesRepo.findBySs7SccpId(anyInt())).thenReturn(List.of(sccpRemoteResourcesEntity));
        Mockito.when(sccpSapRepo.findBySs7SccpId(anyInt())).thenReturn(List.of());
        response = objectSs7Service.refreshingSettingSs7Gateway(1);
        checkErrorAssertions(response);

        Mockito.when(sccpSapRepo.findBySs7SccpId(anyInt())).thenReturn(List.of(sccpServiceAccessPointsEntity));
        Mockito.when(sccpMtp3DestRepo.fetchMtp3Destinations(anyInt())).thenReturn(List.of());
        response = objectSs7Service.refreshingSettingSs7Gateway(1);
        checkErrorAssertions(response);

        Mockito.when(sccpMtp3DestRepo.fetchMtp3Destinations(anyInt())).thenReturn(List.of(sccpMtp3DestinationsDTOMock));
        Mockito.when(sccpAddressRepo.findBySs7SccpId(anyInt())).thenReturn(List.of());
        response = objectSs7Service.refreshingSettingSs7Gateway(1);
        checkErrorAssertions(response);

        Mockito.when(sccpAddressRepo.findBySs7SccpId(anyInt())).thenReturn(List.of(sccpAddressesEntity));
        Mockito.when(sccpRulesRepo.fetchSccpRules(anyInt())).thenReturn(List.of());
        response = objectSs7Service.refreshingSettingSs7Gateway(1);
        checkErrorAssertions(response);
    }

    public static void checkErrorAssertions(ApiResponse response) {
        assertNotNull(response);
        assertEquals("error", response.message());
        assertEquals(500, response.status());
    }

    public static void checkOkAssertions(ApiResponse response) {
        assertNotNull(response);
        assertEquals("success", response.message());
        assertEquals(200, response.status());
    }
}