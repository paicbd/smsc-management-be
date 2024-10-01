package com.smsc.management.app.ss7.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.smsc.management.utils.StaticMethods;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class RedisSs7DTO {
	private String name;
	@JsonProperty("network_id")
	private int networkId;
	@JsonProperty("mno_id")
	private int mnoId;
	private RedisM3ua m3ua;
	private RedisSccp sccp;
	private TcapDTO tcap;
	private MapDTO map;
	
	@Getter @Setter
	public class RedisM3ua {
		private M3uaDTO general;
		private Redisassociations associations;
		@JsonProperty("application_servers")
		private List<M3uaApplicationServerDTO> applicationServers;
		private List<M3uaRoutesDTO> routes;
	}
	
	@Getter @Setter
	public class Redisassociations {
		private List<M3uaSocketsDTO> sockets;
	    private List<M3uaAssociationsDTO> associations;
	}
	
	@Getter @Setter
	public class RedisSccp {
		private SccpDTO general;
		@JsonProperty("remote_resources")
		private List<SccpRemoteResourcesDTO> remoteResources;
		@JsonProperty("service_access_points")
		private RedisSap serviceAccessPoints;
		private List<SccpAddressesDTO> addresses;
		private List<SccpRulesDTO> rules;
	}
	
	@Getter @Setter
	public class RedisSap {
		@JsonProperty("service_access")
		private List<SccpServiceAccessPointsDTO> serviceAccess;
		@JsonProperty("mtp3_destinations")
		private List<SccpMtp3DestinationsDTO> mtp3Destinations;
	}
	
	@Override
	public String toString() {
		return StaticMethods.toJson(this);
	}
}
