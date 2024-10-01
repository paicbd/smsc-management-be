package com.smsc.management.app.diameter.dto;

import com.smsc.management.utils.StaticMethods;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DiameterDTO {
	private int id;
	
	@NotNull(message = "name cannot be null")
	@NotBlank(message = "name cannot be empty")
	private String name;
	
	private int enabled = 0; // 0 disabled, 1 enabled, 2 logical removed
	
	@NotNull(message = "name cannot be null")
	@Pattern(regexp = "^(SCTP|TCP)$", message = "connection must be SCTP or TCP")
	private String connection;
	
	@Override
	public String toString() {
		return StaticMethods.toJson(this);
	}
}
