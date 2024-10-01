package com.smsc.management.config;

import com.smsc.management.integration.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.JedisCluster;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class BeansDefinitionTest extends BaseIntegrationTest {
    @Autowired
    BeansDefinition beansDefinition;

    @Test
    void webMvcTaskExecutor() {
        assertNotNull(beansDefinition.webMvcTaskExecutor());
    }

    @Test
    void jedisCluster() {
        JedisCluster jedisCluster = beansDefinition.jedisCluster();
        assertNotNull(jedisCluster);
    }
}