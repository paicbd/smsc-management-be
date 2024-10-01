package com.smsc.management.app.provider.service;

import static com.smsc.management.utils.Constants.BIND_DEFAULT_STATUS;
import static com.smsc.management.utils.Constants.BIND_STARTED_STATUS;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

import com.smsc.management.exception.SmscBackendException;
import com.smsc.management.utils.AppProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import com.smsc.management.app.credit.custom.HandlerCreditByServiceProvider;
import com.smsc.management.app.credit.custom.HandlerServiceProvider;
import com.smsc.management.app.provider.dto.CallbackHeaderHttpDTO;
import com.smsc.management.app.provider.dto.ParseServiceProviderDTO;
import com.smsc.management.app.provider.dto.RedisServiceProviderDTO;
import com.smsc.management.utils.ApiResponse;
import com.smsc.management.app.provider.dto.ServiceProviderDTO;
import com.smsc.management.app.provider.model.entity.CallbackHeaderHttp;
import com.smsc.management.app.provider.model.entity.ServiceProvider;
import com.smsc.management.app.provider.mapper.ServiceProviderMapper;
import com.smsc.management.app.provider.model.repository.CallbackHeaderHttpRepository;
import com.smsc.management.app.provider.model.repository.ServiceProviderRepository;
import com.smsc.management.utils.Constants;
import com.smsc.management.utils.ResponseMapping;
import com.smsc.management.app.sequence.SequenceNetworksIdGenerator;
import com.smsc.management.utils.UtilsBase;
import lombok.extern.slf4j.Slf4j;

/**
 * Service provider for processing various operations related to service providers.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ServiceProviderService {
	private final ServiceProviderRepository serviceProviderRepository;
	private final CallbackHeaderHttpRepository callbackHeaderRepo;
	private final ServiceProviderMapper serviceProviderMapper;
	private final UtilsBase utilsBase;
	private final SequenceNetworksIdGenerator seqServiceProvider;
	
	// to handler balance
	private final HandlerCreditByServiceProvider handlerBalance;
	private final HandlerServiceProvider handlerSp;
	private final AppProperties appProperties;

	/**
     * Retrieves the list of service providers.
     *
     * @return ResponseDTO containing the list of service providers
     */
	public ApiResponse getServiceProvider() {
		try {
			List<ServiceProvider> servicesProvidersEntity = serviceProviderRepository.findByEnabledNot(Constants.DELETED_STATUS);
			List<ParseServiceProviderDTO>  servicesProviders = new ArrayList<>();
			for (ServiceProvider sp : servicesProvidersEntity) {
				ParseServiceProviderDTO serviceProvider = serviceProviderMapper.toDTO(sp);
				List<CallbackHeaderHttp> headers = callbackHeaderRepo.findByNetworkId(sp.getNetworkId());
				List<CallbackHeaderHttpDTO> headersDTO = serviceProviderMapper.toDTOCallbackHeader(headers);
				serviceProvider.setCallbackHeadersHttp(headersDTO);
				
				servicesProviders.add(serviceProvider);
			}
			return ResponseMapping.successMessage("get Service Provider request success", servicesProviders);
		} catch (Exception e) {
			log.error("new service provider error on getServiceProvider: {}", e.getMessage());
			return ResponseMapping.exceptionMessage("Service provider was end with error", e);
		}
	}
	
	/**
     * Creates a new service provider.
     *
     * @param newProvider The service provider data to be created
     * @return ResponseDTO indicating the outcome of the create operation
     */
	public ApiResponse create(ServiceProviderDTO newProvider){
		try {
			this.utilsBase.isRequestDlrAndTransmitterBind(newProvider.getBindType(), newProvider.isRequestDlr(), newProvider.getProtocol());
			this.utilsBase.validateMaxLengthSpAndGw(newProvider.getPassword(), newProvider.getSystemId(), newProvider.getProtocol());
			this.validateCallbackUrl(newProvider.getCallbackUrl(), newProvider.getProtocol());
			this.validateTokenAuthorization(newProvider);
			
			if (!existsSystemIdAndEnabled(newProvider.getSystemId(), Constants.DELETED_STATUS, 0, "create")){
				List<CallbackHeaderHttpDTO> callHeaders = newProvider.getCallbackHeadersHttp();
				var serviceProviderEntity = serviceProviderMapper.toEntity(newProvider);
				serviceProviderEntity.setNetworkId(seqServiceProvider.getNextNetworkIdSequenceValue("SP"));
				serviceProviderEntity.setStatus(Constants.BIND_DEFAULT_STATUS);
				serviceProviderEntity.setActiveSessionsNumbers(0);
				
				var resultEntity = serviceProviderRepository.save(serviceProviderEntity);
				
				// handler callback security headers
				for (CallbackHeaderHttpDTO header : callHeaders) {
					CallbackHeaderHttp headerEntity = serviceProviderMapper.toEntityCallbackHeader(header);
					headerEntity.setNetworkId(resultEntity.getNetworkId());
					callbackHeaderRepo.save(headerEntity);
				}
				
				socketAndRedisAction(resultEntity.getNetworkId());
				ParseServiceProviderDTO spCreated = serviceProviderMapper.toDTO(resultEntity);
				spCreated.setCallbackHeadersHttp(callHeaders);
				
				var result = ResponseMapping.successMessage("Service Provider added successful.", spCreated);
				log.info("new service provider created: {}", result);
				return result;
			}

			return ResponseMapping.errorMessage("There is already an active system id, you must assign a different system id.");
		} catch (DataIntegrityViolationException e) {
			throw e;
		}  catch (Exception e) {
			log.error("new service provider with error: {}", e.getMessage());
			return ResponseMapping.exceptionMessage("Service Provider was end with error", e);
		}
		
	}

	/**
	 * Updates an existing service provider.
	 *
	 * @param id            The ID of the service provider to update
	 * @param updateProvider The updated service provider data
	 * @return ResponseDTO indicating the outcome of the update operation
	 */
	public ApiResponse update(int id, ServiceProviderDTO updateProvider) {
		try {
			this.utilsBase.isRequestDlrAndTransmitterBind(updateProvider.getBindType(), updateProvider.isRequestDlr(), updateProvider.getProtocol());
			this.utilsBase.validateMaxLengthSpAndGw(updateProvider.getPassword(), updateProvider.getSystemId(), updateProvider.getProtocol());
			this.validateCallbackUrl(updateProvider.getCallbackUrl(), updateProvider.getProtocol());
			this.validateTokenAuthorization(updateProvider);
			if (isUniqueSystemId(updateProvider.getSystemId(), id)) {
				ServiceProvider serviceProviderEntity = serviceProviderRepository.findById(id);
				if (serviceProviderEntity != null) {
					if (serviceProviderEntity.getEnabled() == Constants.DELETED_STATUS) {
						return ResponseMapping.errorMessage("Illegal exception it is not possible to modify a deleted account.");
					}
					String currentSystemId = serviceProviderEntity.getSystemId();
					List<CallbackHeaderHttpDTO> callHeaders = updateProvider.getCallbackHeadersHttp();
					updateProvider.setNetworkId(id);

					checkStatusChangeOnUpdateServiceProvider(serviceProviderEntity, updateProvider);
					updateCallbacks(id, callHeaders);

					var resultEntity = serviceProviderRepository.save(serviceProviderEntity);
					socketAndRedisAction(resultEntity.getNetworkId());
					ParseServiceProviderDTO spUpdated = serviceProviderMapper.toDTO(resultEntity);
					spUpdated.setCallbackHeadersHttp(callHeaders);

					// remove objects from current system id if updated from GUI
					if (!currentSystemId.equals(resultEntity.getSystemId())) {
						utilsBase.removeInRedis(appProperties.getServiceProviderKey(), currentSystemId);
						utilsBase.sendNotificationSocket(utilsBase.findDeleteEndpointByProtocolToSP(resultEntity.getProtocol()), currentSystemId);
						handlerSp.removeFromCache(currentSystemId);
						handlerBalance.removeFromCache(currentSystemId);
					}

					return ResponseMapping.successMessage("Service Provider updated successfully.", spUpdated);
				}

				return ResponseMapping.errorMessageNoFound("Service Provider with ID= " + id + " was not found.");
			}
			return ResponseMapping.errorMessage("There is already an active system id, you must assign a different system id.");
		} catch (DataIntegrityViolationException e) {
			throw e;
		}  catch (Exception e) {
			log.error("Update service provider with error: {}", e.getMessage());
			return ResponseMapping.exceptionMessage("Service Provider update failed", e);
		}
	}
	
	/**
     * Finds a system ID and enable status.
     *
     * @param systemId   The system ID to search for
     * @param enabled    The enable status
     * @param networkId  The network ID
     * @param type       The type of operation
     * @return True if the system ID and enable status are found, false otherwise
     */
	public boolean existsSystemIdAndEnabled(String systemId, int enabled, int networkId, String type) {
		try {
			List<ServiceProvider> serviceProviderFound = switch (type) {
				case "create" -> serviceProviderRepository.findBySystemIdAndEnabledNot(systemId, enabled);
				case "update" ->
                        serviceProviderRepository.findBySystemIdAndEnabledNotAndNetworkIdNot(systemId, enabled, networkId);
                default -> throw new IllegalArgumentException("Unexpected value: " + type);
            };
            return !serviceProviderFound.isEmpty();
		}catch (Exception e) {
			log.error("An error has occurred on existsSystemIdAndEnabled{}", e.toString());
		}
		return false;
	}
	
	/**
     * Performs socket and Redis actions for a service provider.
     *
     * @param netWorkId The network ID of the service provider
     * @return True if the action was successful, false otherwise
     */
	public boolean socketAndRedisAction(int netWorkId) {
		ServiceProvider serviceProvider = serviceProviderRepository.findById(netWorkId);
		List<CallbackHeaderHttp> headers = callbackHeaderRepo.findByNetworkId(netWorkId);
		List<CallbackHeaderHttpDTO> headersDTO = serviceProviderMapper.toDTOCallbackHeader(headers);
		RedisServiceProviderDTO serviceProviderDTO = serviceProviderMapper.toServiceProviderDTO(serviceProvider);
		serviceProviderDTO.setCallbackHeadersHttp(headersDTO);
		
		// making token format
		if (!"Api-key".equalsIgnoreCase(serviceProviderDTO.getAuthenticationTypes())) {
			serviceProviderDTO.setToken(serviceProviderDTO.getAuthenticationTypes() + " " + serviceProviderDTO.getToken());
		}
		
		String systemId = serviceProviderDTO.getSystemId();
		
		// routing rules data
		serviceProviderDTO.setDefaultValues();

		//do not rewrite the value of hasAvailableCredit in the service provider object
		Boolean hasAvailableCredit = false;
		if (handlerBalance.isLocalCharging()) {
			try {
				hasAvailableCredit = handlerSp.getConfigForClient(systemId).getHasAvailableCredit();
			} catch (Exception e) {
				log.warn("current has available credit flag for system id {} was no found -> {}", systemId, e.getMessage());
			}
		} else {
			hasAvailableCredit = true;
		}
		
		String endpoint;

		//0 : STOPPED, 1: STARTED, 2: DELETED
		switch (serviceProviderDTO.getEnabled()) {
			case 0, 1:
				endpoint = utilsBase.findUpdateEndpointByProtocolToSP(serviceProviderDTO.getProtocol());
				handlerSp.addToCache(serviceProviderDTO.getSystemId(), serviceProviderDTO);
				handlerBalance.addNewHandlerBalance(serviceProviderDTO.getSystemId(), serviceProviderDTO.getBalance());
				break;
			case 2:
				endpoint = utilsBase.findDeleteEndpointByProtocolToSP(serviceProviderDTO.getProtocol());
				handlerSp.removeFromCache(serviceProviderDTO.getSystemId());
				handlerBalance.removeFromCache(serviceProviderDTO.getSystemId());
				break;
			default:
				throw new IllegalArgumentException("Unexpected value: " + serviceProviderDTO.getEnabled());
		}
		
		serviceProviderDTO.setHasAvailableCredit(hasAvailableCredit);
		String serviceProviderString = serviceProviderDTO.toString();
        if (Objects.nonNull(serviceProviderString)) {
        	utilsBase.storeInRedis(appProperties.getServiceProviderKey(), serviceProviderDTO.getSystemId(), serviceProviderString);
            utilsBase.sendNotificationSocket(endpoint, serviceProviderDTO.getSystemId());
            return true;
        }
        return false;
	}
	
	public void validateCallbackUrl(String callbackUrl, String protocol) {
		if (protocol.equalsIgnoreCase("http") && (callbackUrl == null || callbackUrl.isBlank())) {
			throw new SmscBackendException("callback url is required to HTTP protocol");
		}
	}
	
	public void validateTokenAuthorization(ServiceProviderDTO sp) {
		try {
			if ("http".equalsIgnoreCase(sp.getProtocol())) {
				if ("basic".equalsIgnoreCase(sp.getAuthenticationTypes())) {
					if (sp.getPasswd() == null || sp.getPasswd().isBlank()) {
						throw new SmscBackendException("User name is required to Basic authentication");
					}
					if (sp.getUserName() == null || sp.getUserName().isBlank()) {
						throw new SmscBackendException("password is required to Basic authentication");
					}

					String authString = sp.getUserName() + ":" + sp.getPasswd();
					String authEncoded = Base64.getEncoder().encodeToString(authString.getBytes());
					sp.setToken(authEncoded);
				} else if (!"undefined".equalsIgnoreCase(sp.getAuthenticationTypes()) && (sp.getToken() == null || sp.getToken().isBlank())) {
					throw new SmscBackendException("Token is required to " + sp.getAuthenticationTypes() + " authentication");
				}
			}
		} catch (Exception e) {
			throw new SmscBackendException(e.getMessage());
		}
	}

	private void checkStatusChangeOnUpdateServiceProvider(ServiceProvider serviceProviderEntity, ServiceProviderDTO updateProvider) {
		// Required for SMPP server: startedSp change the status to -> Started
		boolean startedSp = serviceProviderEntity.getEnabled() == Constants.DISABLED_STATUS && updateProvider.getEnabled() == Constants.ENABLED_STATUS;
		boolean stoppedSp = serviceProviderEntity.getEnabled() == Constants.ENABLED_STATUS && updateProvider.getEnabled() == Constants.DISABLED_STATUS;

		serviceProviderMapper.updateEntityFromDTO(updateProvider, serviceProviderEntity);
		if (startedSp) {
			serviceProviderEntity.setStatus(BIND_STARTED_STATUS);
		} else if (stoppedSp && "http".equalsIgnoreCase(serviceProviderEntity.getProtocol())) {
			// change the status to stopped for http because the core does not raise the connection dynamically.
			serviceProviderEntity.setStatus(BIND_DEFAULT_STATUS);
		}
	}

	private void updateCallbacks(int networkId, List<CallbackHeaderHttpDTO> callHeaders) {
		// handler callback security headers
		callbackHeaderRepo.deleteAllByNetworkId(networkId);

		for (CallbackHeaderHttpDTO header : callHeaders) {
			CallbackHeaderHttp headerEntity = serviceProviderMapper.toEntityCallbackHeader(header);
			headerEntity.setNetworkId(networkId);
			callbackHeaderRepo.save(headerEntity);
		}
	}

	public boolean isUniqueSystemId(String systemId, int networkId) {
		try {
			return serviceProviderRepository.findBySystemIdAndEnabledNotAndNetworkIdNot(systemId, Constants.DELETED_STATUS, networkId).isEmpty();
		} catch (Exception e) {
			log.error("An error has occurred on isUniqueSystemId {}", e.toString());
		}
		return false;
	}
}
