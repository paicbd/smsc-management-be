package com.smsc.management.app.diameter.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smsc.management.app.diameter.model.entity.Diameter;

public interface DiameterRepository extends JpaRepository<Diameter, Integer> {
	Diameter findById(int id);
	List<Diameter> findByEnabledNot(int enabled);
	Diameter findByEnabled(int enabled);
}
