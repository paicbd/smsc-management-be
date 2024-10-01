package com.smsc.management.app.mno.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smsc.management.app.mno.dto.OperatorMNODTO;
import com.smsc.management.utils.ApiResponse;
import com.smsc.management.app.mno.service.OperatorMnoService;

import jakarta.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mno")
public class OperatorMnoController {
	private final OperatorMnoService processor;

	@GetMapping
	public ResponseEntity<ApiResponse> listProvider(){
		ApiResponse result = processor.getProvider();
		return ResponseEntity.status(result.status()).body(result);
	}
	
	@PostMapping("/create")
	public ResponseEntity<ApiResponse> createProvider(@RequestBody @Valid OperatorMNODTO provider){
		ApiResponse result = processor.create(provider);
		return ResponseEntity.status(result.status()).body(result);
	}
	
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<ApiResponse> deleteProvider(@PathVariable int id){
		ApiResponse result = processor.delete(id);
		return ResponseEntity.status(result.status()).body(result);
	}
	
	@PutMapping("/update/{id}")
	public ResponseEntity<ApiResponse> updateProvider(@RequestBody @Valid OperatorMNODTO provider, @PathVariable int id){
		ApiResponse result = processor.update(id, provider);
		return ResponseEntity.status(result.status()).body(result);
	}
}
