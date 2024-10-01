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
@Table(name = "diameter_peers")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@SequenceGenerator(name = "diameter_peers_id_seq", sequenceName = "diameter_peers_id_seq", allocationSize = 1)
public class Peers {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "diameter_peers_id_seq")
	private int id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "ip")
	private String ip;
	
	@Column(name = "host")
	private String host;
	
	@Column(name = "port")
	private Integer port;
	
	@Column(name = "attempt_connect")
	private boolean attemptConnect;
	
	@Column(name = "rating")
	private int rating;
	
	@Column(name = "type")
	private String type;
	
	@Column(name = "uri")
	private String uri;
	
	@Column(name = "use_uri_as_fqdn")
	private boolean useUriAsFqdn;
	
	@Column(name = "diameter_id")
	private int diameterId;
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name="diameter_id", insertable=false, updatable=false)
	private Diameter diameter;
}
