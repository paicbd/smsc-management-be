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
@Table(name = "diameter_parameters")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@SequenceGenerator(name = "diameter_parameters_id_seq", sequenceName = "diameter_parameters_id_seq", allocationSize = 1)
public class Parameters {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "diameter_parameters_id_seq")
	private int id;
	
	@Column(name = "accept_undefined_peer")
	private boolean acceptUndefinedPeer;
	
	@Column(name = "duplicate_protection")
	private boolean duplicateProtection;
	
	@Column(name = "duplicate_timer")
	private int duplicateTimer;
	
	@Column(name = "duplicate_size")
	private int duplicateSize;
	
	@Column(name = "queue_size")
	private int queueSize;
	
	@Column(name = "message_time_out")
	private int messageTimeOut;
	
	@Column(name = "stop_time_out")
	private int stopTimeOut;
	
	@Column(name = "cea_time_out")
	private int ceaTimeOut;
	
	@Column(name = "iac_time_out")
	private int iacTimeOut;
	
	@Column(name = "dwa_time_out")
	private int dwaTimeOut;
	
	@Column(name = "dpa_time_out")
	private int dpaTimeOut;
	
	@Column(name = "rec_time_out")
	private int recTimeOut;
	
	@Column(name = "peer_fsm_thread_count")
	private int peerFsmThreadCount;
	
	@Column(name = "diameter_id")
	private int diameterId;
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name="diameter_id", insertable=false, updatable=false)
	private Diameter diameter;
}
