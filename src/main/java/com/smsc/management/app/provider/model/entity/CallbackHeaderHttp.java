package com.smsc.management.app.provider.model.entity;

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
@Table(name = "Callback_header_http")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@SequenceGenerator(name = "Callback_header_http_id_seq", sequenceName = "Callback_header_http_id_seq", allocationSize = 1)
public class CallbackHeaderHttp {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Callback_header_http_id_seq")
	private int id;
	
	@Column(name = "header_name")
	private String headerName;
	
	@Column(name = "header_value")
	private String headerValue;
	
	@Column(name="network_id")
	private int networkId;
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name="network_id", insertable=false, updatable=false)
	private ServiceProvider serviceProvider;
}
