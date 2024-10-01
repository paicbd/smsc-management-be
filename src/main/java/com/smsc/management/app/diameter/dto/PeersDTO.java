package com.smsc.management.app.diameter.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import com.smsc.management.utils.StaticMethods;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonInclude(Include.NON_NULL)
public class PeersDTO {
	private int id;
	
	@NotNull(message = "name cannot be null")
	@NotBlank(message = "name cannot be empty")
	private String name;
	
	@NotNull(message = "ip cannot be null")
	private String ip;
	
	@NotNull(message = "host cannot be null")
	private String host;
	
	private Integer port;
	
	@JsonProperty("attempt_connect")
	private boolean attemptConnect;
	
	private int rating;
	
	@NotNull(message = "type cannot be null")
	@Pattern(regexp = "^(PCRF|OCS)$", message = "type must be PCRF or OCS")
	private String type;
	
	private String uri;
	
	@JsonProperty("use_uri_as_fqdn")
	private boolean useUriAsFqdn;
	
	@NotNull(message = "diameter_id cannot be null")
	@JsonProperty("diameter_id")
	private Integer diameterId;
	
	@Override
	public String toString() {
		return StaticMethods.toJson(this);
	}
}
