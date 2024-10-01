package com.smsc.management.app.settings.controller;

import com.smsc.management.integration.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HandlerStatusControllerTest extends BaseIntegrationTest {
    @Autowired
    HandlerStatusController handlerStatusController;

    @Test
    void handleStatus() {
        assertDoesNotThrow(() -> handlerStatusController.handleStatus());
    }

    @Test
    void handlerServer() {
        var response = handlerStatusController.handlerServer("STARTED");
        assertEquals(200, response.getStatusCode().value());
        assertEquals(Boolean.TRUE, response.getBody());
    }

    @Test
    void getSmppServerConfig() {
        var response = handlerStatusController.getSmppServerConfig();
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    @Test
    void handlerServerHttp() {
        var response = handlerStatusController.handlerServerHttp("application_name", "STARTED");
        assertEquals(404, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    @Test
    void handlerStatusAllServerHttp() {
        var response = handlerStatusController.handlerStatusAllServerHttp("STARTED");
        assertEquals(404, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    @Test
    void getHttpServerConfig() {
        var response = handlerStatusController.getHttpServerConfig();
        assertEquals(404, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }
}