package com.smsc.management.config;

import com.paicbd.smsc.dto.UtilsRecords;
import com.paicbd.smsc.utils.Converter;
import com.smsc.management.utils.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import redis.clients.jedis.JedisCluster;

import java.util.List;
import java.util.concurrent.Executor;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BeansDefinition {
    private final AppProperties properties;

    @Bean(name = "webMvcTaskExecutor")
    public Executor webMvcTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(5);
        taskExecutor.setMaxPoolSize(50);
        taskExecutor.setQueueCapacity(100);
        taskExecutor.initialize();
        return taskExecutor;
    }

    @Bean
    public JedisCluster jedisCluster() {
        log.info("### Creating JedisCluster bean");
        return Converter.paramsToJedisCluster(getJedisClusterParams(properties.getRedisNodes(), properties.getMaxTotal(),
                properties.getMinIdle(), properties.getMaxIdle(), properties.isBlockWhenExhausted()));
    }
    private UtilsRecords.JedisConfigParams getJedisClusterParams(List<String> nodes, int maxTotal, int minIdle, int maxIdle, boolean blockWhenExhausted) {
        return new UtilsRecords.JedisConfigParams(nodes, maxTotal, minIdle, maxIdle, blockWhenExhausted);
    }
}
