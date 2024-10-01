package com.smsc.management.app.diameter.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import com.smsc.management.utils.StaticMethods;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class LocalPeerDTO {
	private int id;
	
	@NotNull(message = "uri cannot be null")
	@NotEmpty(message = "uri cannot be empty")
	private String uri;
	
	@NotNull(message = "ip_address cannot be null")
	@NotEmpty(message = "ip_address cannot be empty")
	@JsonProperty("ip_address")
	private String ipAddress;
	
	@JsonProperty("vendor_id")
	private int vendorId;
	
	@NotNull(message = "product_name cannot be null")
	@NotEmpty(message = "product_name cannot be empty")
	@JsonProperty("product_name")
	private String productName;
	
	@NotNull(message = "firmware_revision cannot be null")
	@JsonProperty("firmware_revision")
	private int firmwareRevision;

	@NotNull(message = "realm cannot be null")
	@NotEmpty(message = "realm cannot be empty")
	private String realm;
	
	@NotNull(message = "diameter_id cannot be null")
	@JsonProperty("diameter_id")
	private Integer diameterId;
	
	@Override
	public String toString() {
		return StaticMethods.toJson(this);
	}
}
