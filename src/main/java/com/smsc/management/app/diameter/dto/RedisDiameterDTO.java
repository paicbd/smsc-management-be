package com.smsc.management.app.diameter.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.smsc.management.utils.StaticMethods;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@JsonInclude(Include.NON_NULL)
public class RedisDiameterDTO {
	private int id;
	private String name;
	private int enabled;
	private String connection;
	@JsonProperty("use_pcrf")
	private boolean usePcrf = false;
	@JsonProperty("use_ocs")
	private boolean useOcs = false;
	
	@JsonProperty("local_peer")
	private LocalPeerDTO localPeer;
	
	private ParametersDTO parameters;
	
	private Network network;
	
	@Getter @Setter
	public class Network {
		private List<PeersDTO> peers = new ArrayList<>();
		private List<RedisRealms> realms = new ArrayList<>();
	}
	
	@Getter @Setter
	public class RedisRealms {
		private int id;
		private String name;
		private String domain;
		private String peers;
		@JsonProperty("local_action")
		private String localAction;
		private boolean dynamic;
		@JsonProperty("exp_time")
		private int expTime;
		@JsonProperty("application")
		private RedisApplications application;
	}
	
	@Getter @Setter
	public class RedisApplications {
		@JsonProperty("vendor_id")
		private int vendorId;
		@JsonProperty("auth_appl_id")
		private int authApplId;
		@JsonProperty("acct_appl_id")
		private int acctApplId;
	}
	
	@Override
	public String toString() {
		return StaticMethods.toJson(this);
	}
}
