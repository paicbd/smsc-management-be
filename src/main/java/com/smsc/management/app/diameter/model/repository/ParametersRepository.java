package com.smsc.management.app.diameter.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smsc.management.app.diameter.model.entity.Parameters;


public interface ParametersRepository extends JpaRepository<Parameters, Integer> {
	Parameters findById(int id);
	
	Parameters findByDiameterId(int diameterId);
}
