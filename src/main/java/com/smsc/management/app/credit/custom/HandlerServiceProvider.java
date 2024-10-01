package com.smsc.management.app.credit.custom;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.smsc.management.exception.SmscBackendException;
import com.smsc.management.utils.AppProperties;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smsc.management.app.provider.dto.RedisServiceProviderDTO;
import com.smsc.management.utils.UtilsBase;
import static com.smsc.management.utils.Constants.UPDATE_SERVICE_SMPP_PROVIDER_ENDPOINT;
import static com.smsc.management.utils.Constants.UPDATE_SERVICE_HTTP_PROVIDER_ENDPOINT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisCluster;

@Component
@RequiredArgsConstructor
@Slf4j
public class HandlerServiceProvider {
	private final Map<String, RedisServiceProviderDTO> cacheServiceProvider = new ConcurrentHashMap<>();
	private final JedisCluster jedisCluster;
	private final ObjectMapper objectMapper = new ObjectMapper();
	private final UtilsBase utilsBase;

	private final AppProperties appProperties;
		
	/**
     * Add and update a service provider configuration to the cache.
     *
     * @param systemId The system ID of the service provider
     * @param config   The service provider configuration
     */
	public void addToCache(String systemId, RedisServiceProviderDTO config) {
		cacheServiceProvider.put(systemId, config);
		log.info("Added service provider to cache -> {}", config.toString());
    }
	
	/**
     * Removes a service provider configuration from the cache.
     *
     * @param systemId The system ID of the service provider to remove
     */
	public void removeFromCache(String systemId) {
		cacheServiceProvider.remove(systemId);
		log.warn("service provider with system ID {} removed.", systemId);
    }
	
	/**
     * Retrieves the configuration for a specific service provider.
     *
     * @param systemId The system ID of the service provider
     * @return The service provider configuration
     * @throws JsonProcessingException if the readValue method throws and exception
     */
	public RedisServiceProviderDTO getConfigForClient(String systemId)  throws JsonProcessingException {
		RedisServiceProviderDTO spData = cacheServiceProvider.get(systemId);
		
		if (spData == null) {
			String spStr = jedisCluster.hget(appProperties.getServiceProviderKey(), systemId);
			if (spStr == null || spStr.isBlank()) {
				throw new SmscBackendException("System ID " + systemId + " was not found");
			}
			spData = objectMapper.readValue(spStr, RedisServiceProviderDTO.class);
        	this.addToCache(systemId, spData);
		}
		
		return spData;
    }
	
	/**
	 * Updates the cache, Redis, and socket with the provided Redis service provider configuration.
	 *
	 * @param config The Redis service provider configuration to update in the cache, Redis, and socket.
	 */
	public void updateCacheAndRedisAndSocket(RedisServiceProviderDTO config) {
		// update local current hash map
		cacheServiceProvider.put(config.getSystemId(), config);
		
		// update Redis
		utilsBase.storeInRedis(appProperties.getServiceProviderKey(), config.getSystemId(), config.toString());
		
		// send notify by socket by protocol
		String endpoint = UPDATE_SERVICE_SMPP_PROVIDER_ENDPOINT;
		if ("http".equalsIgnoreCase(config.getProtocol())) {
			endpoint = UPDATE_SERVICE_HTTP_PROVIDER_ENDPOINT;
		}
        utilsBase.sendNotificationSocket(endpoint, config.getSystemId());
	}
}
