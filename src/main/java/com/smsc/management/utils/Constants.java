package com.smsc.management.utils;

import com.paicbd.smsc.utils.Generated;

@Generated
public class Constants {
	private Constants() {
		throw new IllegalStateException("Utility class");
	}
	
	public static final int DISABLED_STATUS = 0;
	public static final int ENABLED_STATUS = 1;
	public static final int DELETED_STATUS = 2;
	
	public static final String BIND_DEFAULT_STATUS = "STOPPED";
	public static final String BIND_STARTED_STATUS = "STARTED";

	// endpoint smpp gateway
	public static final String UPDATE_GATEWAY_ENDPOINT = "/app/updateGateway";
	public static final String CONNECT_GATEWAY_ENDPOINT = "/app/connectGateway";
	public static final String STOP_GATEWAY_ENDPOINT = "/app/stopGateway";
	public static final String DELETE_GATEWAY_ENDPOINT = "/app/deleteGateway";

	// endpoint to http gateway
	public static final String UPDATE_HTTP_GATEWAY_ENDPOINT = "/app/http/updateGateway";
	public static final String CONNECT_HTTP_GATEWAY_ENDPOINT = "/app/http/connectGateway";
	public static final String STOP_HTTP_GATEWAY_ENDPOINT = "/app/http/stopGateway";
	public static final String DELETE_HTTP_GATEWAY_ENDPOINT = "/app/http/deleteGateway";
	
	public static final String UPDATE_SS7_GATEWAY_ENDPOINT = "/app/ss7/updateGateway";
	public static final String DELETE_SS7_GATEWAY_ENDPOINT = "/app/ss7/deleteGateway";
	
	public static final String UPDATE_ERROR_CODE_MAPPING_ENDPOINT = "/app/updateErrorCodeMapping"; // Receive mno_id as String
	public static final String DELETE_SERVICE_SMPP_PROVIDER_ENDPOINT = "/app/smpp/serviceProviderDeleted";
	public static final String DELETE_SERVICE_HTTP_PROVIDER_ENDPOINT = "/app/http/serviceProviderDeleted";
	public static final String UPDATE_SERVICE_SMPP_PROVIDER_ENDPOINT = "/app/smpp/updateServiceProvider";
	public static final String UPDATE_SERVICE_HTTP_PROVIDER_ENDPOINT = "/app/http/updateServiceProvider";
	public static final String UPDATE_SERVER_HANDLER_ENDPOINT = "/app/updateServerHandler";
	public static final String UPDATE_HTTP_SERVER_HANDLER_ENDPOINT = "/app/httpUpdateServerHandler";
	
	/*
	 * routing rules
	 */
	public static final String UPDATE_ROUTING_RULES_ENDPOINT = "/app/update/routingRules";
	public static final String DELETE_ROUTING_RULES_ENDPOINT = "/app/delete/routingRules";
	
	/*
	 * to hash table configurations
	 */
	public static final String CONFIGURATIONS_HASH_TABLE = "configurations";
	public static final String CONFIGURATIONS_SMPP_SERVER_KEY = "smpp_server";
	public static final String CONFIGURATIONS_SMPP_SERVER_VALUE = "{\"state\":\"STARTED\"}";
	
	/* 
	 * general settings
	 */
	public static final String GENERAL_SETTINGS_SMPP_HTTP_ENDPOINT = "/app/generalSettings";
	public static final String GENERAL_SMSC_RETRY_ENDPOINT = "/app/generalSmsRetry";
	public static final String GENERAL_SETTINGS_DIAMETER_ENDPOINT = "/app/generalSettings/diameter";
	public static final String DELETE_GENERAL_SETTINGS_DIAMETER_ENDPOINT = "/app/generalSettings/delete/diameter";
	
	/*
	 * common variables
	 */
	public static final String LOCAL_CHARGING = "USE_LOCAL_CHARGING";
	public static final String SMSC_ACCOUNT_SETTINGS = "SMSC_ACCOUNT_SETTINGS";
	public static final String KEY_MAX_PASSWORD_LENGTH = "max_password_length";
	public static final String KEY_MAX_SYSTEM_ID_LENGTH = "max_system_id_length";
}
