package com.smsc.management.app.provider.model.entity;

import com.smsc.management.app.catalog.model.entity.BalanceType;
import com.smsc.management.app.catalog.model.entity.BindStatuses;
import com.smsc.management.app.catalog.model.entity.InterfazVersions;
import com.smsc.management.app.catalog.model.entity.NpiCatalog;
import com.smsc.management.app.sequence.SequenceNetworksId;
import com.smsc.management.app.catalog.model.entity.TonCatalog;
import com.smsc.management.app.catalog.model.entity.BindsTypes;
import com.smsc.management.utils.EntityBase;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 * The sequence will be assigned only odd numbers, it is updated in the DataInitializer.java
 */
@Entity
@Table(name = "service_provider")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class ServiceProvider extends EntityBase {
	@Id
	@Column(name="network_id")
	private int networkId;
	@OneToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name="network_id", insertable=false, updatable=false)
	private SequenceNetworksId serviceProvider;
	
	@Column(name="name",columnDefinition = "text NOT NULL")
	private String name;
	@Column(name="system_id",columnDefinition = "text NOT NULL")
	private String systemId;
	@Column(name="password",columnDefinition = "text NOT NULL")
	private String password;
	@Column(name="system_type",columnDefinition = "text NOT NULL")
	private String systemType;
	
	@Column(name="interface_version",columnDefinition = "text NOT NULL DEFAULT 'IF_34'")
	private String interfaceVersion;
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name="interface_version", insertable=false, updatable=false)
	private InterfazVersions interfazVersion;
	
	@Column(name="sessions_number", columnDefinition = "int NOT NULL DEFAULT 1")
	private int sessionsNumber;
	
	@Column(name = "address_ton", columnDefinition = "int NOT NULL DEFAULT 0")
	private int addressTon;
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name="address_ton", insertable=false, updatable=false)
	private TonCatalog tonCatalog;
	
	@Column(name = "address_npi",columnDefinition = "int NOT NULL DEFAULT 0")
	private int addressNpi;
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name="address_npi", insertable=false, updatable=false)
	private NpiCatalog npiCatalog;
	
	@Column(name="address_range", columnDefinition = "text NOT NULL DEFAULT '^[0-9a-zA-Z]*'")
	private String addressRange;
	
	@Column(name="balance_type",columnDefinition = "text DEFAULT 'PREPAID'")
	private String balanceType;
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name="balance_type", insertable=false, updatable=false)
	private BalanceType balanceTypes;
	
	@Column(columnDefinition = "bigint DEFAULT 0")
	private Long balance;
	
	@Column(columnDefinition = "int DEFAULT 1")
	private int tps;
	
	@Column(columnDefinition = "int DEFAULT 0")
	private int validity;
	
	@Column(columnDefinition = "int DEFAULT 0")
	private int enabled; // account state
	
	@Column(name="enquire_link_period", columnDefinition = "int DEFAULT 30000")
	private int enquireLinkPeriod;
	
	@Column(name="pdu_timeout", columnDefinition = "int DEFAULT 5000")
	private int pduTimeout;
	
	@Column(name="request_dlr", columnDefinition = "bool DEFAULT false")
	private boolean requestDlr;
	
	@Column(name="status", columnDefinition = "text DEFAULT 'CLOSED'")
	private String status;
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name="status", insertable=false, updatable=false)
	private BindStatuses bindStatus;
	
	@Column(name="active_sessions_numbers")
	private int activeSessionsNumbers;

	@Column(name="protocol")
	private String protocol;

	@Column(name="contact_name")
	private String contactName;

	@Column(name="email")
	private String email;

	@Column(name="phone_number")
	private String phoneNumber;
	
	@Column(name="callback_url")
	private String callbackUrl;
	
	@Column(name = "authentication_types")
	private String authenticationTypes;
	
	@Column(name = "header_security_name")
	private String headerSecurityName;
	
	@Column(name = "user_name")
	private String userName;
	
	@Column(name = "passwd")
	private String passwd;
	
	@Column(name = "token")
	private String token;

	@Column(name="bind_type",columnDefinition = "text NOT NULL DEFAULT 'TRANSCEIVER'")
	private String bindType;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name="bind_type", insertable=false, updatable=false)
	private BindsTypes bindsTypes;
}
