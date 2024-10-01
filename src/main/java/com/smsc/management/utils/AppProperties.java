package com.smsc.management.utils;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.smsc.management.utils.Constants.CONFIGURATIONS_HASH_TABLE;
import static com.smsc.management.utils.Constants.CONFIGURATIONS_SMPP_SERVER_KEY;

@Getter
@Component
public class AppProperties {
    @Value("${cluster.nodes}")
    private List<String> redisNodes;

    @Value("${cluster.threadPool.maxTotal}")
    private int maxTotal;

    @Value("${cluster.threadPool.maxIdle}")
    private int maxIdle;

    @Value("${cluster.threadPool.minIdle}")
    private int minIdle;

    @Value("${cluster.threadPool.blockWhenExhausted}")
    private boolean blockWhenExhausted;

    @Value("${cors.allowed.origins}")
    private String allowedOrigins;

    @Value("${app.security.jwt.secret-key}")
    private String secretKey;

    @Value("${app.security.jwt.expiration}")
    private long jwtExpiration;

    @Value("${server.key.serviceProviders}")
    private String serviceProviderKey;

    @Value("${server.balanceHandler.hashName}")
    private String spBalanceHandler;

    @Value("${server.key.gateways}")
    private String gatewaysKey;

    @Value("${server.balancePeriod.store}")
    private int balancePeriodToStore;

    @Value("${server.key.errorCodeMapping}")
    private String errorCodeMapping;

    @Value("${websocket.header.name}")
    private String websocketHeaderName;

    @Value("${websocket.header.value}")
    private String websocketHeaderValue;

    @Value("${server.apiKeyHeader.name}")
    private String apiKeyHeaderName;

    @Value("${server.apiKey.value}")
    private String apiKeyHeaderValue;

    @Value("${app.root.user}")
    private String rootUser;

    @Value("${app.root.password}")
    private String rootPassword;

    @Value("${server.key.routingRules}")
    private String hashRoutingRules;

    @Value("${smpp.server.configurationHashName}")
    private String configurationHash = CONFIGURATIONS_HASH_TABLE;

    @Value("${smpp.server.keyName}")
    private String serverKey = CONFIGURATIONS_SMPP_SERVER_KEY;

    @Value("${general.settings}")
    private String hashTableGeneralSettings = "general_settings";

    @Value("${general.settings.key.smppHttp}")
    private String hashKeyGeneralSettingsSmppHttp = "smpp_http";

    @Value("${general.settings.key.smscRetry}")
    private String hashKeyGeneralSettingsSmscRetry = "smsc_retry";
}

