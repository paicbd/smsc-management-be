package com.smsc.management.app.diameter.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smsc.management.app.diameter.model.entity.Applications;

public interface ApplicationsRepository extends JpaRepository<Applications, Integer> {
	Applications findById(int id);
}
