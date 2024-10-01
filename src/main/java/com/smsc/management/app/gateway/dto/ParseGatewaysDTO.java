package com.smsc.management.app.gateway.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.smsc.management.utils.StaticMethods;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class ParseGatewaysDTO {
	@JsonProperty("network_id")
	private int networkId;
	
	private String name;
	
	@JsonProperty("system_id")
	private String systemId;
	
	private String password;
	
	private String ip;
	
	private int port;
	
	@JsonProperty("bind_type")
	private String bindType;
	
	@JsonProperty("system_type")
	private String systemType;
	
	@JsonProperty("interface_version")
	private String interfaceVersion;
	
	@JsonProperty("sessions_number")
	private int sessionsNumber;
	
	@JsonProperty("address_ton")
	private int addressTon;
	
	@JsonProperty("address_npi")
	private int addressNpi;
	
	@JsonProperty("address_range")
	private String addressRange;
	
	private int tps;
	
	@JsonProperty("enabled")
	private int enabled; // account state
	
	@JsonProperty("enquire_link_period")
	private int enquireLinkPeriod;
	
	@JsonProperty("request_dlr")
	private boolean requestDlr;
	
	@JsonProperty("no_retry_error_code")
	private String noRetryErrorCode;
	
	@JsonProperty("retry_alternate_destination_error_code")
	private String retryAlternateDestinationErrorCode;
	
	@JsonProperty("bind_timeout")
	private int bindTimeout;
	
	@JsonProperty("bind_retry_period")
	private int bindRetryPeriod;
	
	@JsonProperty("pdu_timeout")
	private int pduTimeout;
	
	@JsonProperty("pdu_degree")
	private int pduDegree;
	
	@JsonProperty("thread_pool_size")
	private int threadPoolSize;
	
	@JsonProperty("mno_id")
	private int mnoId;
	
	@JsonProperty("status")
	private String status;
	
	@JsonProperty("active_sessions_numbers")
	private int activeSessionsNumbers;
	
	@JsonProperty("enquire_link_timeout")
	private int enquireLinkTimeout;
	
	@JsonProperty("tlv_message_receipt_id")
    private boolean tlvMessageReceiptId;
	
	@JsonProperty("message_id_decimal_format")
    private boolean messageIdDecimalFormat;
	
	@JsonProperty("protocol")
	private String protocol;
	
	@JsonProperty("auto_retry_error_code")
	private String autoRetryErrorCode;

	@JsonProperty("encoding_iso88591")
	private int encodingIso88591;

	@JsonProperty("encoding_gsm7")
	private int encodingGsm7;

	@JsonProperty("encoding_ucs2")
	private int encodingUcs2;
	
	@JsonProperty("split_message")
	private boolean splitMessage;
	
	@JsonProperty("split_smpp_type")
	private String splitSmppType;

	@Override
	public String toString() {
		return StaticMethods.toJson(this);
	}
}
