package com.smsc.management.app.diameter.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "diameter_realms")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@SequenceGenerator(name = "diameter_realms_id_seq", sequenceName = "diameter_realms_id_seq", allocationSize = 1)
public class Realms {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "diameter_realms_id_seq")
	private int id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "domain")
	private String domain;
	
	@Column(name = "local_action")
	private String localAction;
	
	@Column(name = "dynamic")
	private boolean dynamic;
	
	@Column(name = "exp_time")
	private int expTime;
	
	@Column(name = "peer_id")
	private int peerId;
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name="peer_id", insertable=false, updatable=false)
	private Peers peers;
	
	@Column(name = "application_id")
	private int applicationId;
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name="application_id", insertable=false, updatable=false)
	private Applications applications;
}
