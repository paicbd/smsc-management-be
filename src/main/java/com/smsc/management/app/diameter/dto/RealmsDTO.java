package com.smsc.management.app.diameter.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.smsc.management.utils.StaticMethods;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Setter
@Getter
@AllArgsConstructor
public class RealmsDTO {
	private int id;
	
	@NotNull(message = "name cannot be null")
	@NotBlank(message = "name cannot be empty")
	private String name;
	
	@NotNull(message = "domain cannot be null")
	@NotBlank(message = "domain cannot be empty")
	private String domain;
	
	@NotNull(message = "local_action cannot be null")
	@Pattern(regexp = "^(LOCAL|RELAY|PROXY|REDIRECT)$", message = "local_action must be LOCAL or RELAY or PROXY or REDIRECT")
	@JsonProperty("local_action")
	private String localAction;
	
	@NotNull(message = "dynamic cannot be null")
	private boolean dynamic;
	
	@NotNull(message = "exp_time cannot be null")
	@JsonProperty("exp_time")
	private int expTime;
	
	@NotNull(message = "peer_id cannot be null")
	@JsonProperty("peer_id")
	private int peerId;
	
	@NotNull(message = "application_id cannot be null")
	@JsonProperty("application_id")
	private int applicationId;
	
	@Override
	public String toString() {
		return StaticMethods.toJson(this);
	}
}
