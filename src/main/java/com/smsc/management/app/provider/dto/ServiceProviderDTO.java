package com.smsc.management.app.provider.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.smsc.management.regex.ValidRegex;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class ServiceProviderDTO {
	@JsonProperty("network_id")
	private int networkId; 

	@NotBlank(message = "Name cannot be empty")
	@NotNull(message = "Name cannot be null")
	private String name;
	
	@NotBlank(message = "Name cannot be empty")
	@NotNull(message = "Name cannot be null")
	@Pattern(regexp = "[^,\\'}{\\]\\[\\)\\(\\\"\\\\]+", message = "The following characters are not supported for systemId: ,'}{][)(\"\\")
	@JsonProperty("system_id")
	private String systemId;
	
	@NotBlank(message = "Name cannot be empty")
	@NotNull(message = "Name cannot be null")
	private String password;
	
	@Size(min = 0, max = 13, message = "System type must be between 1 and 13 characters")
	@JsonProperty("system_type")
	private String systemType;
	
	@JsonProperty("interface_version")
	private String interfaceVersion;

	@Min(value = 1, message = "sessionsNumber should not be less than 1")
	@Max(value = 50, message = "sessionsNumber should not be greater than 50")
	@JsonProperty("sessions_number")
	private int sessionsNumber;

	@JsonProperty("bind_type")
	private String bindType;
		
	@NotNull(message = "Address TON cannot be null")
	@Min(value = 0, message = "addressTon should not be less than 0")
	@JsonProperty("address_ton")
	private int addressTon;
	
	@NotNull(message = "Address NPI cannot be null")
	@Min(value = 0, message = "addressNpi should not be less than 0")
	@JsonProperty("address_npi")
	private int addressNpi;
	
	@ValidRegex
	@JsonProperty("address_range")
	private String addressRange;
	
	@JsonProperty("balance_type")
	private String balanceType;
	
	@NotNull(message = "Balance cannot be null")
	@PositiveOrZero(message = "Balance should not be less than 0")
	private Long balance;
	
	@Min(value = 1, message = "TPS should not be less than 1")
	private int tps;
	
	private int validity = 0;

	@Min(value = 0, message = "enabled should not be less than 0")
	@Max(value = 2, message = "enabled should not be greater than 2")
	@JsonProperty("enabled")
	private int enabled; // account state

	@JsonProperty("enquire_link_period")
	private int enquireLinkPeriod;

	@JsonProperty("pdu_timeout")
	@Min(value = 1000, message = "PDU timeout should not be less than 1000 ms")
	@Max(value = 60000, message = "PDU timeout should not be greater than 60000 ms")
	private int pduTimeout;

	@JsonProperty("request_dlr")
	private boolean requestDlr;

	@Pattern(regexp = "^(SMPP|HTTP)$", message = "Protocol must be SMPP or HTTP")
	@NotBlank(message = "Protocol cannot be empty")
	@JsonProperty("protocol")
	private String protocol;

	@JsonProperty("contact_name")
	private String contactName;

	@Email(message = "Email should be valid")
	@JsonProperty("email")
	private String email;

	@JsonProperty("phone_number")
	private String phoneNumber;
	
	@JsonProperty("callback_url")
	private String callbackUrl;
	
	@Pattern(regexp = "^(Basic|Bearer|Api-key|Undefined)$", message = "authentication types must be Undefined or Basic or Bearer or Api-key")
	@JsonProperty("authentication_types")
	private String authenticationTypes;
	
	@JsonProperty("header_security_name")
	private String headerSecurityName = "Authorization";
	
	@JsonProperty("user_name")
	private String userName;
	
	@JsonProperty("passwd")
	private String passwd;
	
	@NotNull(message = "token cannot be null")
	@JsonProperty("token")
	private String token;
	
	@JsonProperty("callback_headers_http")
	private List<CallbackHeaderHttpDTO> callbackHeadersHttp = new ArrayList<>();
	
	public void setAddressRange(String addressRange) {
		this.addressRange = "^[0-9a-zA-Z]*";
		if (addressRange != null && !addressRange.isBlank()) {
			this.addressRange = addressRange;
		}
	}
}
