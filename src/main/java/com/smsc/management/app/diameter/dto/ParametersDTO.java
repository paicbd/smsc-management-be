package com.smsc.management.app.diameter.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import com.smsc.management.utils.StaticMethods;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class ParametersDTO {
	private int id;
	
	@JsonProperty("accept_undefined_peer")
	private boolean acceptUndefinedPeer;
	
	@JsonProperty("duplicate_protection")
	private boolean duplicateProtection;
	
	@JsonProperty("duplicate_timer")
	private int duplicateTimer;
	
	@JsonProperty("duplicate_size")
	private int duplicateSize;
	
	@JsonProperty("queue_size")
	private int queueSize;
	
	@JsonProperty("message_time_out")
	private int messageTimeOut;
	
	@JsonProperty("stop_time_out")
	private int stopTimeOut;
	
	@JsonProperty("cea_time_out")
	private int ceaTimeOut;
	
	@JsonProperty("iac_time_out")
	private int iacTimeOut;
	
	@JsonProperty("dwa_time_out")
	private int dwaTimeOut;
	
	@JsonProperty("dpa_time_out")
	private int dpaTimeOut;
	
	@JsonProperty("rec_time_out")
	private int recTimeOut;
	
	@JsonProperty("peer_fsm_thread_count")
	private int peerFsmThreadCount;
	
	@NotNull(message = "diameter_id cannot be null")
	@JsonProperty("diameter_id")
	private Integer diameterId;
	
	@Override
	public String toString() {
		return StaticMethods.toJson(this);
	}
}
