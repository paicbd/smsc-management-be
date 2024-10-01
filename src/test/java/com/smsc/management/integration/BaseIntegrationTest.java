package com.smsc.management.integration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;

import java.io.File;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {
    private static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine");

    public static DockerComposeContainer<?> redisCluster =
            new DockerComposeContainer<>(new File("src/test/resources/docker-compose.yml"))
                    .withExposedService("redis-node-0", 8910,
                            new LogMessageWaitStrategy().withRegEx(".*Ready to accept connections.*\\s"))
                    .withExposedService("redis-node-1", 8911,
                            new LogMessageWaitStrategy().withRegEx(".*Ready to accept connections.*\\s"))
                    .withExposedService("redis-node-2", 8912,
                            new LogMessageWaitStrategy().withRegEx(".*Ready to accept connections.*\\s"));

    static  {
        log.info("Starting PostgreSQL container...");
        postgres.start();
        log.info("PostgreSQL container started. Starting Redis cluster...");
        redisCluster.start();
        log.info("Redis cluster started. Waiting for Redis cluster to be ready...");
    }
}
