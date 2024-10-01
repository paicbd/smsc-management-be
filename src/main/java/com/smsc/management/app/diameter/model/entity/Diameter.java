package com.smsc.management.app.diameter.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "diameter")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@SequenceGenerator(name = "diameter_id_seq", sequenceName = "diameter_id_seq", allocationSize = 1)
public class Diameter {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "diameter_id_seq")
	private int id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "enabled")
	private int enabled; // 0 disabled, 1 enabled, 2 logical removed
	
	@Column(name = "connection")
	private String connection;
}
