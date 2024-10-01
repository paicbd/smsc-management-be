package com.smsc.management.app.catalog.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "binds_types")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class BindsTypes {
	@Id
	private String _type;
}
