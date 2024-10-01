package com.smsc.management.websocket;

import com.smsc.management.app.settings.service.StatusHandlerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Sinks;

@Controller
@RequiredArgsConstructor
@Slf4j(topic = "WebSocketController")
public class WebSocketController {
    private final Sinks.Many<String> sink;
    private final StatusHandlerService statusHandlerService;

    private static final String SERVICE_PROVIDER = "sp";
    private static final String GATEWAY = "gw";

    @MessageMapping("/session-confirm")
    public void handleSessionConfirm(String sessionConfirm) {
        log.info("Session confirm received: {}", sessionConfirm);
    }

    @MessageMapping("/response-smpp-server")
    public void handleResponseSmppServer(String responseSmppServer) {
        log.info("SMPP Server: {}", responseSmppServer);
    }
    
    @MessageMapping("/response-smpp-client")
    public void handleResponseSmppClient(String responseSmppClient) {
        log.info("SMPP Client: {}", responseSmppClient);
    }
    
    @MessageMapping("/response-http-server")
    public void handleResponseHttpServer(String responseSmppServer) {
        log.info("HTTP Server: {}", responseSmppServer);
    }
    
    @MessageMapping("/response-http-client")
    public void handleResponseHttpClient(String responseSmppClient) {
        log.info("HTTP Client: {}", responseSmppClient);
    }

    @MessageMapping("/handler-status")
    public void handleStatus(String msg) {
        if (msg == null || msg.trim().isEmpty()) {
            log.error("The message is null or empty");
            return;
        }

        String[] msgSplit = msg.trim().split(",");
        if (msgSplit.length != 4) {
            log.error("Invalid message, expected length is 4, but got {}: {}", msgSplit.length, msg);
            return;
        }

        String entity = msgSplit[0].trim();
        String systemId = msgSplit[1].trim();
        String param = msgSplit[2].trim();
        String value = msgSplit[3].trim();

        if (entity.equalsIgnoreCase(SERVICE_PROVIDER)) {
            statusHandlerService.updateSpStatusOnDatabase(systemId, param, value);
        } else if (entity.equalsIgnoreCase(GATEWAY)) {
            statusHandlerService.updateGwStatusOnDatabase(systemId, param, value);
        } else {
            log.error("Invalid entity: {}", entity);
            return;
        }

        emitMessage(msg);
        log.info("Notification received: {}", msg);
    }


    @MessageMapping("/confirm-instance-connection")
    public void handleConfirmInstanceConnection(String confirmInstanceConnection) {
        log.info("Confirm instance connection: {}", confirmInstanceConnection);
    }

    @MessageMapping("/confirm-instance-disconnection")
    public void handleConfirmInstanceDisconnection(String confirmInstanceDisconnection) {
        log.info("Confirm instance disconnection: {}", confirmInstanceDisconnection);
    }

    public synchronized void emitMessage(String msg) {
        sink.tryEmitNext(msg);
    }
}
