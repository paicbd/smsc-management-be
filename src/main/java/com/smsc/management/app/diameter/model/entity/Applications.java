package com.smsc.management.app.diameter.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "diameter_applications")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class Applications {
	@Id
	private int id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "vendor_id")
	private int vendorId;
	
	@Column(name = "auth_appl_id")
	private int authApplId;
	
	@Column(name = "acct_appl_id")
	private int acctApplId;
	
	@Column(name = "type")
	private String type;
}
