package com.smsc.management.app.credit.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RatingManagementDTO {
	@JsonProperty("system_id")
	private String systemId;
	
	@JsonProperty("has_available_credit")
	private Boolean hasAvailableCredit;
	
	@JsonProperty("available_credit")
	private Long availableCredit;
	
	@JsonProperty("tps")
	private int tps;
	
	@JsonProperty("credit_sales_history")
	private List<CreditSalesHistoryDTO> creditSalesHistory;
}
