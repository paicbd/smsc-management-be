package com.smsc.management.init;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.smsc.management.utils.UtilsBase;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import static com.smsc.management.utils.Constants.CONFIGURATIONS_HASH_TABLE;
import static com.smsc.management.utils.Constants.CONFIGURATIONS_SMPP_SERVER_KEY;
import static com.smsc.management.utils.Constants.CONFIGURATIONS_SMPP_SERVER_VALUE;

@Component
@RequiredArgsConstructor
public class RedisInitializer {
	private final UtilsBase utils;
	
	@Value("${smpp.server.configurationHashName}")
    private String configurationHash = CONFIGURATIONS_HASH_TABLE;

    @Value("${smpp.server.keyName}")
    private String serverKey = CONFIGURATIONS_SMPP_SERVER_KEY;
    
    @Value("${smpp.server.value}")
    private String serverDefaultValue = CONFIGURATIONS_SMPP_SERVER_VALUE;
    
	/*
	 * look for key smpp_server if it does not exist then initialize
	 */
	@PostConstruct
	private void serverConfigurationsInit () {
		String keyResult = utils.getInRedis(configurationHash, serverKey);
		if (keyResult == null || keyResult.isBlank()) {
			utils.storeInRedis(configurationHash, serverKey, serverDefaultValue);
		}
	}
}
