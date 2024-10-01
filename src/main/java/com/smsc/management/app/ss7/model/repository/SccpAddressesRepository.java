package com.smsc.management.app.ss7.model.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.smsc.management.app.ss7.model.entity.SccpAddresses;

public interface SccpAddressesRepository extends JpaRepository<SccpAddresses, Integer> {
	SccpAddresses findById(int id);
	List<SccpAddresses> findBySs7SccpId(int sccpId);
}
