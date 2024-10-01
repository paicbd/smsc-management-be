package com.smsc.management.app.ss7.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smsc.management.app.ss7.model.entity.Ss7Gateways;

public interface Ss7GatewaysRepository extends JpaRepository<Ss7Gateways, Integer> {
	Ss7Gateways findById(int id);
	List<Ss7Gateways> findByEnabledNot(int enabled);
	
	List<Ss7Gateways> findByMnoIdAndEnabledNot(int mnoId, int enabled);
	
	Ss7Gateways findByNetworkId(int id);
}
