package com.smsc.management.app.provider.service;

import com.smsc.management.app.credit.custom.HandlerCreditByServiceProvider;
import com.smsc.management.app.credit.custom.HandlerServiceProvider;
import com.smsc.management.app.provider.dto.ParseServiceProviderDTO;
import com.smsc.management.app.provider.dto.RedisServiceProviderDTO;
import com.smsc.management.app.provider.dto.ServiceProviderDTO;
import com.smsc.management.app.provider.mapper.ServiceProviderMapper;
import com.smsc.management.app.provider.model.entity.CallbackHeaderHttp;
import com.smsc.management.app.provider.model.entity.ServiceProvider;
import com.smsc.management.app.provider.model.repository.CallbackHeaderHttpRepository;
import com.smsc.management.app.provider.model.repository.ServiceProviderRepository;
import com.smsc.management.app.sequence.SequenceNetworksIdGenerator;
import com.smsc.management.utils.ApiResponse;
import com.smsc.management.utils.AppProperties;
import com.smsc.management.utils.Constants;
import com.smsc.management.utils.UtilsBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
class ServiceProviderServiceTest {

    @Mock
    ServiceProviderRepository serviceProviderRepository;

    @Mock
    CallbackHeaderHttpRepository callbackHeaderRepo;

    @Mock
    ServiceProviderMapper serviceProviderMapper;

    @Mock
    UtilsBase utilsBase;

    @Mock
    SequenceNetworksIdGenerator seqServiceProvider;

    @Mock
    HandlerCreditByServiceProvider handlerBalance;

    @Mock
    HandlerServiceProvider handlerSp;

    @Mock
    AppProperties appProperties;

    @InjectMocks
    ServiceProviderService serviceProviderService;

    @Test
    void getServiceProviderExceptionTest() {
        when(serviceProviderRepository.findByEnabledNot(anyInt())).thenThrow(new RuntimeException("RuntimeException"));
        ApiResponse response = serviceProviderService.getServiceProvider();
        assertNotNull(response);
        assertEquals(500, response.status());
    }

    @Test
    void createAlreadyReferencedProviderTest() {
        ServiceProviderDTO serviceProviderDTO = getBasicServiceProviderDto();
        when(serviceProviderRepository.findBySystemIdAndEnabledNot(anyString(), anyInt())).thenReturn(List.of(new ServiceProvider()));
        ApiResponse response = serviceProviderService.create(serviceProviderDTO);
        assertEquals(400, response.status());
    }

    @Test
    void createProviderDataIntegrityViolationExceptionTest() {
        ServiceProviderDTO serviceProviderDTO = getBasicServiceProviderDto();
        when(serviceProviderRepository.findBySystemIdAndEnabledNot(anyString(), anyInt())).thenReturn(null);
        when(serviceProviderMapper.toEntity(any())).thenReturn(new ServiceProvider());
        when(serviceProviderRepository.save(any())).thenThrow(new DataIntegrityViolationException("DataIntegrityViolationException"));
        assertThrows(DataIntegrityViolationException.class, () -> serviceProviderService.create(serviceProviderDTO));
    }

    @Test
    void createProviderAnyExceptionTest() {
        ServiceProviderDTO serviceProviderDTO = getBasicServiceProviderDto();
        when(serviceProviderRepository.findBySystemIdAndEnabledNot(anyString(), anyInt())).thenThrow(new RuntimeException());
        ApiResponse response = serviceProviderService.create(serviceProviderDTO);
        assertEquals(500, response.status());
    }

    @Test
    void updateServiceProviderNotFoundExceptionTest() {
        ServiceProviderDTO serviceProviderDTO = getBasicServiceProviderDto();
        ApiResponse response = serviceProviderService.update(-1, serviceProviderDTO);
        assertNotNull(response);
        assertEquals(404, response.status());
    }

    @Test
    void updateServiceProviderReferencedExceptionTest() {
        ServiceProviderDTO serviceProviderDTO = getBasicServiceProviderDto();
        when(serviceProviderRepository.findBySystemIdAndEnabledNotAndNetworkIdNot(anyString(), anyInt(), anyInt())).thenReturn(List.of(new ServiceProvider()));
        ApiResponse response = serviceProviderService.update(-1, serviceProviderDTO);
        assertNotNull(response);
        assertEquals(400, response.status());
    }

    @Test
    void updateServiceProviderDeletedExceptionTest() {
        ServiceProviderDTO serviceProviderDTO = getBasicServiceProviderDto();
        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.setEnabled(Constants.DELETED_STATUS);
        when(serviceProviderRepository.findById(anyInt())).thenReturn(serviceProvider);
        ApiResponse response = serviceProviderService.update(-1, serviceProviderDTO);
        assertNotNull(response);
        assertEquals(400, response.status());
    }

    @Test
    void updateServiceProviderDataIntegrityExceptionTest() {
        ServiceProviderDTO serviceProviderDTO = getBasicServiceProviderDto();
        when(serviceProviderRepository.findById(anyInt())).thenReturn(new ServiceProvider());
        when(serviceProviderRepository.save(any())).thenThrow(new DataIntegrityViolationException("DataIntegrityViolationException"));
        assertThrows(DataIntegrityViolationException.class, () -> serviceProviderService.update(-1, serviceProviderDTO));
    }

    @Test
    void updateServiceProviderAnyExceptionTest() {
        ServiceProviderDTO serviceProviderDTO = getBasicServiceProviderDto();
        when(serviceProviderRepository.findById(anyInt())).thenThrow(new RuntimeException());
        ApiResponse response = serviceProviderService.update(-1, serviceProviderDTO);
        assertNotNull(response);
        assertEquals(500, response.status());
    }

    @Test
    void updateServiceProviderStartedTest() {
        testServiceProviderByEnabledStatus(Boolean.TRUE, null);
    }

    @Test
    void updateServiceProviderStoppedTest() {
        testServiceProviderByEnabledStatus(Boolean.FALSE, null);
    }

    @Test
    void updateServiceProviderStoppedAndHttpTest() {
        testServiceProviderByEnabledStatus(Boolean.FALSE, "http");
    }

    @Test
    void updateServiceProviderSystemIdNotEqualsTest() {
        ServiceProviderDTO serviceProviderDTO = getBasicServiceProviderDto();
        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.setSystemId("System1");
        serviceProvider.setNetworkId(10);
        when(serviceProviderRepository.findById(anyInt())).thenReturn(serviceProvider);
        ServiceProvider anotherServiceProvider = new ServiceProvider();
        anotherServiceProvider.setSystemId("System2");
        anotherServiceProvider.setNetworkId(10);
        when(serviceProviderRepository.save(serviceProvider)).thenReturn(anotherServiceProvider);
        when(callbackHeaderRepo.findByNetworkId(anyInt())).thenReturn(List.of(new CallbackHeaderHttp()));
        when(serviceProviderMapper.toServiceProviderDTO(serviceProvider)).thenReturn(new RedisServiceProviderDTO());
        when(serviceProviderMapper.toDTO(anotherServiceProvider)).thenReturn(new ParseServiceProviderDTO());
        ApiResponse response = serviceProviderService.update(-1, serviceProviderDTO);
        assertNotNull(response);
        assertEquals(200, response.status());
    }

    @Test
    void testFindSystemIdAndEnable() {
        boolean result = serviceProviderService.existsSystemIdAndEnabled("SystemId", 0, 1, "anotherType");
        assertFalse(result);
    }

    @Test
    void testFindSystemIdAndEnableAnyException() {
        boolean result = serviceProviderService.existsSystemIdAndEnabled("SystemId", 0, 1, "update");
        assertFalse(result);
    }

    @Test
    void socketAndRedisActionCaseWithApiKey() {
        socketAndRedisActionCaseTest(0, Boolean.TRUE);
    }

    @Test
    void socketAndRedisActionDeletedTest() {
        socketAndRedisActionCaseTest(2, Boolean.FALSE);
    }

    @Test
    void socketAndRedisActionUnexpectedTest() {
        socketAndRedisActionCaseTest(3, Boolean.FALSE);
    }

    @Test
    void validateCallbackUrlNullCallAndHttpTest() {
        assertThrows(Exception.class, () -> serviceProviderService.validateCallbackUrl(null, "http"));
    }

    @Test
    void validateCallbackUrlEmptyCallAndHttpTest() {
        assertThrows(Exception.class, () -> serviceProviderService.validateCallbackUrl("", "http"));
    }

    @Test
    void validateCallbackUrlFilledCallAndHttpTest() {
        assertDoesNotThrow(() -> serviceProviderService.validateCallbackUrl("http://callback.com/", "http"));
    }

    @Test
    void validateCallbackUrlFilledCallAndNoHttpTest() {
        assertDoesNotThrow(() -> serviceProviderService.validateCallbackUrl("http://callback.com/", "smpp"));
    }

    @Test
    void validateCallbackUrlNullCallAndNoHttpTest() {
        assertDoesNotThrow(() -> serviceProviderService.validateCallbackUrl(null, "smpp"));
    }

    @Test
    void validateCallbackUrlEmptyCallAndNoHttpTest() {
        assertDoesNotThrow(() -> serviceProviderService.validateCallbackUrl("", "smpp"));
    }

    @Test
    void validateTokenAuthorizationBasicNullPwdTest() {
        validateTokenAuthorizationUserAndPasswordTest("password", null);
    }

    @Test
    void validateTokenAuthorizationBasicEmptyPwdTest() {
        validateTokenAuthorizationUserAndPasswordTest("password", "");
    }

    @Test
    void validateTokenAuthorizationBasicFilledPwdTest() {
        validateTokenAuthorizationUserAndPasswordTest("password", "paSSw0Rd");
    }

    @Test
    void validateTokenAuthorizationBasicNullUserTest() {
        validateTokenAuthorizationUserAndPasswordTest("userName", null);
    }

    @Test
    void validateTokenAuthorizationBasicEmptyUserTest() {
        validateTokenAuthorizationUserAndPasswordTest("userName", "");
    }

    @Test
    void validateTokenAuthorizationBasicFilledUserTest() {
        validateTokenAuthorizationUserAndPasswordTest("userName", "TestUser");
    }

    @Test
    void validateTokenAuthorizationNoBasicNullTokenAuthTest() {
        validateTokenAuthorizationNoBasicAuthTest("bearer", null);
    }

    @Test
    void validateTokenAuthorizationNoBasicEmptyTokenAuthTest() {
        validateTokenAuthorizationNoBasicAuthTest("bearer", "");
    }

    @Test
    void validateTokenAuthorizationNoBasicWithTokenAuthTest() {
        validateTokenAuthorizationNoBasicAuthTest("bearer", "sdsdsdsd");
    }

    @Test
    void validateTokenAuthorizationNoBasicWithTokenAuthUndefTest() {
        validateTokenAuthorizationNoBasicAuthTest("undefined", "sdsdsdsd");
    }

    @Test
    void isUniqueSystemIdAnyExceptionTest() {
        when(serviceProviderRepository.findBySystemIdAndEnabledNotAndNetworkIdNot(anyString(), anyInt(), anyInt())).thenThrow(new RuntimeException());
        boolean response = serviceProviderService.isUniqueSystemId("", 0);
        assertFalse(response);
    }

    ServiceProviderDTO getBasicServiceProviderDto() {
        ServiceProviderDTO serviceProviderDTO = new ServiceProviderDTO();
        serviceProviderDTO.setSystemId("System1");
        serviceProviderDTO.setPasswd("pAssW0rd");
        serviceProviderDTO.setProtocol("SMPP");
        serviceProviderDTO.setBindType("TRANSCEIVER");
        serviceProviderDTO.setCallbackUrl("http://127.0.0.1/URL");
        return serviceProviderDTO;
    }

    void testServiceProviderByEnabledStatus(boolean isStarted, String protocol) {
        ServiceProviderDTO serviceProviderDTO = getBasicServiceProviderDto();
        serviceProviderDTO.setEnabled(isStarted ? 1 : 0);
        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.setSystemId("System1");
        serviceProvider.setEnabled(isStarted ? 0 : 1);
        serviceProvider.setNetworkId(10);
        serviceProvider.setProtocol(protocol);
        when(serviceProviderRepository.findById(anyInt())).thenReturn(serviceProvider);
        when(serviceProviderRepository.save(serviceProvider)).thenReturn(serviceProvider);
        when(callbackHeaderRepo.findByNetworkId(anyInt())).thenReturn(List.of(new CallbackHeaderHttp()));
        when(serviceProviderMapper.toServiceProviderDTO(serviceProvider)).thenReturn(new RedisServiceProviderDTO());
        when(serviceProviderMapper.toDTO(serviceProvider)).thenReturn(new ParseServiceProviderDTO());
        ApiResponse response = serviceProviderService.update(-1, serviceProviderDTO);
        assertNotNull(response);
        assertEquals(200, response.status());
    }

    void socketAndRedisActionCaseTest(int enabledState, boolean usesApiKey) {
        when(serviceProviderRepository.findById(anyInt())).thenReturn(new ServiceProvider());
        RedisServiceProviderDTO redisServiceProviderDTO = new RedisServiceProviderDTO();
        redisServiceProviderDTO.setEnabled(enabledState);
        if (usesApiKey) {
            redisServiceProviderDTO.setAuthenticationTypes("Api-key");
        }
        when(serviceProviderMapper.toServiceProviderDTO(any())).thenReturn(redisServiceProviderDTO);
        if (enabledState <= 2) {
            boolean socketAndRedisActionResult = serviceProviderService.socketAndRedisAction(-1);
            assertTrue(socketAndRedisActionResult);
        } else {
            assertThrows(IllegalArgumentException.class, () -> serviceProviderService.socketAndRedisAction(-1));
        }
    }

    void validateTokenAuthorizationUserAndPasswordTest(String key, String value) {
        ServiceProviderDTO serviceProviderDTO = new ServiceProviderDTO();
        serviceProviderDTO.setProtocol("http");
        serviceProviderDTO.setAuthenticationTypes("basic");
        if (key.equalsIgnoreCase("password")) {
            serviceProviderDTO.setPasswd(value);
            assertThrows(Exception.class, () -> serviceProviderService.validateTokenAuthorization(serviceProviderDTO));
        } else {
            serviceProviderDTO.setPasswd("PAssW0Rd");
            serviceProviderDTO.setUserName(value);
            if (value != null && !value.isBlank()) {
                assertDoesNotThrow(() -> serviceProviderService.validateTokenAuthorization(serviceProviderDTO));
            } else {
                assertThrows(Exception.class, () -> serviceProviderService.validateTokenAuthorization(serviceProviderDTO));
            }
        }
    }

    void validateTokenAuthorizationNoBasicAuthTest(String authorizationType, String token) {
        ServiceProviderDTO serviceProviderDTO = new ServiceProviderDTO();
        serviceProviderDTO.setProtocol("http");
        serviceProviderDTO.setAuthenticationTypes(authorizationType);
        serviceProviderDTO.setToken(token);
        if (token != null && !token.isBlank()) {
            assertDoesNotThrow(() -> serviceProviderService.validateTokenAuthorization(serviceProviderDTO));
        } else {
            assertThrows(Exception.class, () -> serviceProviderService.validateTokenAuthorization(serviceProviderDTO));
        }
    }

    @Test
    void createProviderTransmitterAnyExceptionTest() {
       doThrow(new IllegalArgumentException("you must disable the DLR request for the bind type TRANSMITTER"))
                .when(utilsBase).isRequestDlrAndTransmitterBind(anyString(), anyBoolean(), anyString());
        ServiceProviderDTO serviceProviderDTO = getBasicServiceProviderDto();
        serviceProviderDTO.setBindType("TRANSMITTER");
        serviceProviderDTO.setRequestDlr(true);
        serviceProviderDTO.setProtocol("SMPP");
        ApiResponse response = serviceProviderService.create(serviceProviderDTO);
        assertEquals(500, response.status());
    }

    @Test
    void updateServiceProviderTransmitterAnyExceptionTest() {
        doThrow(new IllegalArgumentException("you must disable the DLR request for the bind type TRANSMITTER"))
                .when(utilsBase).isRequestDlrAndTransmitterBind(anyString(), anyBoolean(), anyString());
        ServiceProviderDTO serviceProviderDTO = getBasicServiceProviderDto();
        serviceProviderDTO.setBindType("TRANSMITTER");
        serviceProviderDTO.setRequestDlr(true);
        serviceProviderDTO.setProtocol("SMPP");
        ApiResponse response = serviceProviderService.update(-1, serviceProviderDTO);
        assertEquals(500, response.status());
    }
}
