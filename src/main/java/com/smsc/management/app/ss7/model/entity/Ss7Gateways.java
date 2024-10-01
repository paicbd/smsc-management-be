package com.smsc.management.app.ss7.model.entity;

import com.smsc.management.app.mno.model.entity.OperatorMno;
import com.smsc.management.app.sequence.SequenceNetworksId;
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

@Entity
@Table(name = "ss7_gateways")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class Ss7Gateways extends EntityBase {
	@Id
	@Column(name="network_id")
	private int networkId;
	@OneToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name="network_id", insertable=false, updatable=false)
	private SequenceNetworksId gateways;
	
	@Column(name="name", columnDefinition = "text NOT NULL", unique = true)
	private String name;
	
	@Column(columnDefinition = "int DEFAULT 1")
	private int enabled; // account state
	
	@Column(name="status", columnDefinition = "text default 'STARTED'")
	private String status;
	
	@Column(name="protocol", columnDefinition = "text default 'SS7'")
	private String protocol;
	
	@Column(name="mno_id", columnDefinition = "int Not null")
	private int mnoId;
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name="mno_id", insertable=false, updatable=false)
	private OperatorMno operatorMnoId;
	
	@Column(name = "global_title", columnDefinition = "text default ''")
	private String globalTitle;
	
	@Column(name = "global_title_indicator", columnDefinition = "text default '0100'")
	private String globalTitleIndicator;
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name="global_title_indicator", insertable=false, updatable=false)
	private GlobalTitleIndicator gtIndicators;
	
	@Column(name = "translation_type", columnDefinition = "integer default 0")
	private int translationType;
	
	@Column(name = "smsc_ssn", columnDefinition = "integer default 8")
	private int smscSsn;
	
	@Column(name = "hlr_ssn", columnDefinition = "integer default 6")
	private int hlrSsn;
	
	@Column(name = "msc_ssn", columnDefinition = "integer default 8")
	private int mscSsn;
	
	@Column(name = "map_version", columnDefinition = "integer default 3")
	private int mapVersion;
	
	@Column(name = "split_message", columnDefinition = "boolean default false")
	private boolean splitMessage;
}
