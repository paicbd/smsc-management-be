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
@Table(name = "diameter_local_peer")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@SequenceGenerator(name = "diameter_local_peer_id_seq", sequenceName = "diameter_local_peer_id_seq", allocationSize = 1)
public class LocalPeer {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "diameter_local_peer_id_seq")
	private int id;
	
	@Column(name = "uri")
	private String uri;
	
	@Column(name = "ip_address")
	private String ipAddress;
	
	@Column(name = "vendor_id")
	private int vendorId;
	
	@Column(name = "product_name")
	private String productName;
	
	@Column(name = "firmware_revision")
	private int firmwareRevision;
	
	@Column(name = "realm")
	private String realm;
	
	@Column(name = "diameter_id")
	private int diameterId;
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name="diameter_id", insertable=false, updatable=false)
	private Diameter diameter;
}
