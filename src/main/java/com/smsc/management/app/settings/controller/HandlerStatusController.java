package com.smsc.management.app.settings.controller;

import com.smsc.management.utils.ApiResponse;
import com.smsc.management.app.settings.service.StatusHandlerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;

@Controller
@RequiredArgsConstructor
public class HandlerStatusController {
    private final Flux<String> notifyRealtime;
    private final StatusHandlerService statusHandlerService;

    @GetMapping(value = "/handler-status", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> handleStatus() {
        return this.notifyRealtime;
    }

    @PostMapping("/smpp-server-config/new-status/{newStatus}")
    public ResponseEntity<Boolean> handlerServer(@PathVariable String newStatus) {
        return ResponseEntity.ok(statusHandlerService.updateStatusSmppServer(newStatus.toUpperCase()));
    }
    
    @GetMapping("/smpp-server-config")
    public ResponseEntity<ApiResponse> getSmppServerConfig(){
    	ApiResponse result = statusHandlerService.getSmppServerConfiguration();
		return ResponseEntity.status(result.status()).body(result);
    }
    
    @PostMapping("/http-server-status")
    public ResponseEntity<ApiResponse> handlerServerHttp(@RequestParam(name = "application_name") String applicationName, @RequestParam(name = "new_status") String newStatus) {
    	ApiResponse result = statusHandlerService.updateStatusServerHttp(applicationName, newStatus.toUpperCase());
        return ResponseEntity.status(result.status()).body(result);
    }
    
    @PostMapping("/http-server-status/all")
    public ResponseEntity<ApiResponse> handlerStatusAllServerHttp(@RequestParam(name = "new_status") String newStatus) {
    	ApiResponse result = statusHandlerService.updateStatusAllServerHttp(newStatus.toUpperCase());
        return ResponseEntity.status(result.status()).body(result);
    }
    
    @GetMapping("/http-server-config")
    public ResponseEntity<ApiResponse> getHttpServerConfig(){
    	ApiResponse result = statusHandlerService.getAllHttpServers();
		return ResponseEntity.status(result.status()).body(result);
    }
}
