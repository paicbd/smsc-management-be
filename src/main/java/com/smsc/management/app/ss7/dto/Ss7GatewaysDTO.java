package com.smsc.management.app.ss7.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.smsc.management.utils.StaticMethods;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Ss7GatewaysDTO {
	@JsonProperty("network_id")
	private int networkId;
	
	private String name;
	
	private int enabled; // account state
	
	private String status;
	
	private String protocol = "SS7";
	
	@JsonProperty("mno_id")
	private int mnoId;
	
	@JsonProperty("global_title")
	private String globalTitle;
	
	@JsonProperty("global_title_indicator")
	private String globalTitleIndicator;
	
	@Min(value = 0, message = "translation type should not be less than 0")
	@Max(value = 255, message = "translation type should not be grater than 255")
	@JsonProperty("translation_type")
	private int translationType;
	
	@JsonProperty("smsc_ssn")
	private int smscSsn;
	
	@JsonProperty("hlr_ssn")
	private int hlrSsn;
	
	@JsonProperty("msc_ssn")
	private int mscSsn;
	
	@Min(value = 1, message = "map version should not be less than 1")
	@Max(value = 3, message = "map version should not be grater than 3")
	@JsonProperty("map_version")
	private int mapVersion;
	
	@JsonProperty("split_message")
	private boolean splitMessage = false;
	
	@Override
	public String toString() {
		return StaticMethods.toJson(this);
	}
}
