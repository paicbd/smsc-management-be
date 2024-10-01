package com.smsc.management.app.errorcode.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ErrorCodeDTO {
	private int id;
	
	@NotBlank(message = "Error code cannot be empty")
	@NotNull(message = "Error code cannot be null")
	@Pattern(regexp = "^[0-9]*", message = "Error code should only be in number format")
	@JsonProperty("code")
	private String code;
	
	@NotBlank(message = "Error description cannot be empty")
	@NotNull(message = "Error description cannot be null")
	@JsonProperty("description")
	private String description;
	
	@NotNull(message = "Provider name cannot be null")
	@JsonProperty("mno_id")
	private int mnoId;
}
