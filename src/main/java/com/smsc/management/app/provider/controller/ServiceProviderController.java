package com.smsc.management.app.provider.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smsc.management.utils.ApiResponse;
import com.smsc.management.app.provider.dto.ServiceProviderDTO;
import com.smsc.management.app.provider.service.ServiceProviderService;

import jakarta.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/service-provider")
public class ServiceProviderController {
	private final ServiceProviderService processor;
	
	@GetMapping
	public ResponseEntity<ApiResponse> listServiceProvider(){
		ApiResponse result = processor.getServiceProvider();
		return ResponseEntity.status(result.status()).body(result);
	}

	@PostMapping("/create")
	public ResponseEntity<ApiResponse> createProvider(@RequestBody @Valid ServiceProviderDTO provider){
		ApiResponse result = processor.create(provider);
		return ResponseEntity.status(result.status()).body(result);
	}
	
	@PutMapping("/update/{id}")
	public ResponseEntity<ApiResponse> updateProvider(@RequestBody @Valid ServiceProviderDTO provider, @PathVariable int id){
		ApiResponse result = processor.update(id, provider);
		return ResponseEntity.status(result.status()).body(result);
	}
}
