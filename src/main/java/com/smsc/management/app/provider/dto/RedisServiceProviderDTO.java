package com.smsc.management.app.provider.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.smsc.management.utils.StaticMethods;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

@JsonIgnoreProperties(ignoreUnknown = true)
public class RedisServiceProviderDTO {
	@JsonProperty("network_id")
	private int networkId; 
	private String name;
	@JsonProperty("system_id")
	private String systemId;
	private String password;
	@JsonProperty("system_type")
	private String systemType;
	@JsonProperty("interface_version")
	private String interfaceVersion;
	
	@JsonProperty("address_ton")
	private int addressTon;
	@JsonProperty("address_npi")
	private int addressNpi;
	@JsonProperty("address_range")
	private String addressRange;
	
	private int tps;
	private int validity;
	@JsonProperty("enabled")
	private int enabled; // account state
	@JsonProperty("enquire_link_period")
	private int enquireLinkPeriod;
	@JsonProperty("pdu_timeout")
	private int pduTimeout;
	@JsonProperty("request_dlr")
	private boolean requestDlr;
	@JsonProperty("status")
	private String status;
	@JsonProperty("bind_type")
	private String bindType;
	
	// these keys name are needed in the redis object
	@JsonProperty("max_binds")
	private int sessionsNumber;
	@JsonProperty("current_binds_count")
	private int activeSessionsNumbers;
	@JsonProperty("credit")
	private Long balance;
	
	// This variable name are not needed in the redis object
	@JsonIgnore
	private String balanceType;
	
	// only to redis object format
	@JsonProperty("binds")
	private ArrayList<String> binds = new ArrayList<>();
	@JsonProperty("credit_used")
	private Long creditUsed;
	@JsonProperty("is_prepaid")
	private boolean isPrepaid;
	
	// to management balance credit
	@JsonProperty("has_available_credit")
	private Boolean hasAvailableCredit;

	@JsonProperty("protocol")
	private String protocol;
	@JsonProperty("contact_name")
	private String contactName;
	@JsonProperty("email")
	private String email;
	@JsonProperty("phone_number")
	private String phoneNumber;
	
	@JsonProperty("callback_url")
	private String callbackUrl;
	@JsonProperty("authentication_types")
	private String authenticationTypes;
	@JsonProperty("header_security_name")
	private String headerSecurityName;
	@JsonProperty("token")
	private String token;
	@JsonProperty("callback_headers_http")
	private List<CallbackHeaderHttpDTO> callbackHeadersHttp = new ArrayList<>();
	
	public void setBalanceType(String balanceType) {
		this.isPrepaid = false;
		this.balanceType = balanceType;
		if (balanceType.equalsIgnoreCase("PREPAID")) {
			this.isPrepaid = true;
		}
	}
	
	public void setDefaultValues() {
		this.binds = new ArrayList<>();
		this.creditUsed = 0L;
	}
	
	@Override
	public String toString() {
		return StaticMethods.toJson(this);
	}
}
