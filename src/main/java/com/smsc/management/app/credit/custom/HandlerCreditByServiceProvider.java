package com.smsc.management.app.credit.custom;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.smsc.management.exception.SmscBackendException;
import com.smsc.management.utils.AppProperties;
import org.springframework.stereotype.Component;
import com.smsc.management.app.provider.dto.RedisServiceProviderDTO;
import com.smsc.management.app.settings.model.entity.CommonVariables;
import com.smsc.management.app.provider.model.entity.ServiceProvider;
import com.smsc.management.app.provider.mapper.ServiceProviderMapper;
import com.smsc.management.app.settings.model.repository.CommonVariablesRepository;
import com.smsc.management.app.provider.model.repository.ServiceProviderRepository;
import com.smsc.management.app.credit.service.ProcessCreditService;
import com.smsc.management.utils.UtilsBase;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisCluster;
import static com.smsc.management.utils.Constants.DELETED_STATUS;
import static com.smsc.management.utils.Constants.LOCAL_CHARGING;

/**
 * Component class for handling credits by service providers.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class HandlerCreditByServiceProvider {
	private final UtilsBase utilsBase;
	private final JedisCluster jedisCluster;
	
	/** Map to store the balance of each service provider. */
	private final Map<String, AtomicLong> balance = new ConcurrentHashMap<>();
	private final Map<String, AtomicLong> lastBalance = new ConcurrentHashMap<>();

	private final Map<String, Boolean> localCharging = new ConcurrentHashMap<>();
	
	/** Repository for service providers. */
	private final ServiceProviderRepository serviceProviderRepo;
	private final HandlerServiceProvider handlerSp;
	private final ServiceProviderMapper serviceProviderMapper;

	private final CommonVariablesRepository commonVariableRepo;
	private final AppProperties appProperties;
	
	/**
     * Loads the used credits stored in the database into memory.
     */
	@PostConstruct
	public void init() {
		try {
			// enabled=2 is to not include the removed Service providers.
			List<ServiceProvider> serviceProviders = serviceProviderRepo.findByEnabledNot(2);
			CommonVariables commonVar = commonVariableRepo.findByKey(LOCAL_CHARGING);
			this.putLocalCharging(commonVar != null ? commonVar.getValue() : "true");
	        
			for (ServiceProvider serviceProvider : serviceProviders) {
				storeBalanceInCache(serviceProvider);
	        }
		} catch (Exception e) {
			log.error("Error to load credit used from Redis -> {}", e.getMessage());
		}
	}

	public void storeBalanceInCache(ServiceProvider serviceProvider) {
		try {
			balance.putIfAbsent(serviceProvider.getSystemId(), new AtomicLong(serviceProvider.getBalance()));
			lastBalance.putIfAbsent(serviceProvider.getSystemId(), new AtomicLong(serviceProvider.getBalance()));

			// store in Redis
			String balanceStr = "{\"balance\": " + serviceProvider.getBalance() + ",\"tps\": 0}";
			jedisCluster.hset(appProperties.getSpBalanceHandler(), serviceProvider.getSystemId(), balanceStr);

			// add service provider to cache
			RedisServiceProviderDTO serviceProviderDTO= serviceProviderMapper.toServiceProviderDTO(serviceProvider);
			serviceProviderDTO.setHasAvailableCredit(true);
			if (serviceProvider.getBalance() < 1 && Boolean.TRUE.equals(localCharging.get(LOCAL_CHARGING))) {
				serviceProviderDTO.setHasAvailableCredit(false);
			}
			handlerSp.addToCache(serviceProviderDTO.getSystemId(), serviceProviderDTO);
		} catch (Exception e) {
			throw new SmscBackendException("An error has occurred in storeBalanceInCache(): ", e);
		}
	}
	
	/**
	 * Activate or deactivate local charging
	 * @param value The value to be stored
	 */
	public void putLocalCharging(String value) {
		if (value != null && !value.isBlank()) {
			localCharging.put(LOCAL_CHARGING, Boolean.valueOf(value));
		} else {
			localCharging.put(LOCAL_CHARGING, true);
		}
	}
	
	public boolean isLocalCharging() {
		return localCharging.getOrDefault(LOCAL_CHARGING, true);
	}
	
	/**
	 * Adds a new balance handler for the specified system ID with the given credit balance.
	 * If a balance handler for the system ID already exists, it updates the balance.
	 *
	 * @param systemId      the system ID for which to add the balance handler
	 * @param creditBalance the credit balance to add or update
	 */
	public void addNewHandlerBalance(String systemId, Long creditBalance) {
		balance.computeIfAbsent(systemId, k -> new AtomicLong(creditBalance));
		lastBalance.computeIfAbsent(systemId, k -> new AtomicLong(creditBalance));
	}
	
	/**
     * Removes a service provider configuration from the cache.
     *
     * @param systemId The system ID of the service provider to remove
     */
	public void removeFromCache(String systemId) {
		balance.remove(systemId);
		lastBalance.remove(systemId);
		utilsBase.removeInRedis(appProperties.getSpBalanceHandler(), systemId);
		log.warn("service provider with system ID {} removed in the handler balance map.", systemId);
    }
	
	/**
	 * Retrieves the current balance for the specified system ID.
	 *
	 * @param systemId The system ID for which to retrieve the balance.
	 * @return The current balance for the specified system ID.
	 */
	public Long getCurrentBalance(String systemId) {
		return balance.get(systemId) != null ? balance.get(systemId).get() : null;
	}
	
	/**
     * Adds a new credit package to the balance of a service provider.
     *
     * @param systemId The system ID of the service provider.
     * @param newCreditPackage The amount of the new credit package to add.
     * @return True if the credit package was added successfully, false otherwise.
     * @see ProcessCreditService #updateCreditBalance(CreditSalesHistoryDTO, Integer)
     */
    public boolean addCreditPackage(String systemId, Long newCreditPackage)  {
    	try {
			AtomicLong currentSystemBalanceInfo = balance.get(systemId);
			Objects.requireNonNull(currentSystemBalanceInfo);
    		long currentBalance = currentSystemBalanceInfo.addAndGet(newCreditPackage);
    		// add in last balance to not impact the TPS
    		lastBalance.get(systemId).addAndGet(newCreditPackage);
    		
    		// handler available credit flag
    		if (currentBalance > 0 && currentBalance <= newCreditPackage) {
    			RedisServiceProviderDTO currentConfigSP = handlerSp.getConfigForClient(systemId);
    			updateHasAvailableCredit(currentConfigSP, true);
			}

    		// update in database
    		updateDataBase(systemId, currentBalance);
    		return true;
		}
		catch (Exception e) {
			log.error("Error to add credit used to the system id ({}) -> ", systemId, e);
			throw new SmscBackendException("An error has occurred in addCreditPackage(): ", e);
		}
    }
    
    /**
     * Decreases the credit used by a service provider.
     *
     * @param systemId The system ID of the service provider.
     * @param creditUsed The amount of credit to decrease.
     * @return True if the credit used was decreased successfully, false otherwise.
     */
    public boolean decreaseCreditUsed(String systemId, Long creditUsed)   {
    	try {
    		// pass the negative parameter to cause a decrease in balance
    		long currentBalance = balance.computeIfAbsent(systemId, k -> new AtomicLong(0)).addAndGet(-creditUsed);
    		if (currentBalance < 1 && this.isLocalCharging()) {
    			RedisServiceProviderDTO currentConfigSP = handlerSp.getConfigForClient(systemId);
    			currentConfigSP.setHasAvailableCredit(false);
    			handlerSp.updateCacheAndRedisAndSocket(currentConfigSP);
			}
    		return true;
		}
		catch (Exception e) {
			log.error("Error to decrease credit used to the system id ({}) -> ", systemId, e);
			throw new SmscBackendException("An error has occurred in decreaseCreditUsed(): ", e);
		}
    }
    
    /**
     * Retrieves the current balance map.
     * 
     * @return The current balance map containing system IDs as keys and corresponding AtomicLong values.
     */
    public Map<String, AtomicLong> getCurrentBalanceMap() {
    	return  new ConcurrentHashMap<>(balance);
    }
    
    /**
     * Retrieves the current last balance for the specified system ID.
     * If the system ID is not found, returns a new AtomicLong initialized with 0.
     * 
     * @param systemId The system ID for which to retrieve the last balance.
     * @return The AtomicLong representing the current last balance for the specified system ID.
     */
    public Long getLastBalance(String systemId) {
    	return lastBalance.getOrDefault(systemId, new AtomicLong(0)).get();
    }
    
    /**
     * Sets the last balance for the specified system ID.
     * 
     * @param systemId       The system ID for which to set the last balance.
     * @param currentBalance The AtomicLong representing the current balance to set.
     */
    public void setLastBalance(String systemId, Long currentBalance) {
    	lastBalance.remove(systemId);
    	lastBalance.put(systemId, new AtomicLong(currentBalance));
    }
    
    /**
     * Updates the available credit status for a Redis service provider.
     * If the current available credit status differs from the specified value,
     * the available credit status is updated, and the changes are propagated
     * to the cache, Redis, and socket.
     *
     * @param currentConfigSP   The current Redis service provider configuration.
     * @param hasAvailableCredit The new available credit status to set.
     *                           {@code true} if there is available credit, {@code false} otherwise.
     */
    public void updateHasAvailableCredit(RedisServiceProviderDTO currentConfigSP, boolean hasAvailableCredit) {
    	if (Boolean.TRUE.equals(currentConfigSP.getHasAvailableCredit()) != hasAvailableCredit && this.isLocalCharging()) {
			currentConfigSP.setHasAvailableCredit(hasAvailableCredit);
			handlerSp.updateCacheAndRedisAndSocket(currentConfigSP);
		}
    }
    
    /**
     * Updates the database with the balance for a specific system ID.
     * 
     * @param systemId The ID of the system to update.
     * @param balance The new balance value to set.
     */
    public void updateDataBase(String systemId, Long balance) {
    	try {
    		RedisServiceProviderDTO currentConfigSP = handlerSp.getConfigForClient(systemId);
        	ServiceProvider sp = serviceProviderRepo.findByNetworkIdAndEnabledNot(currentConfigSP.getNetworkId(), DELETED_STATUS);
        	
        	if (sp != null) {
        		int updated = serviceProviderRepo.saveBalance(sp.getNetworkId(), balance);
    			if (updated < 1) {
					log.error("{} rows update to systemId {}", updated, systemId);
				} 
    		}
		} catch (Exception e) {
			log.error("Error systemId {} was not found in the service provider hash map -> {}", systemId, e.getMessage());
		}
    }
}
