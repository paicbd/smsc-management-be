package com.smsc.management.app.gateway.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.smsc.management.app.gateway.model.entity.Gateways;

public interface GatewaysRepository extends CrudRepository<Gateways, Integer> {
	Gateways findById(int id);
	List<Gateways> findByEnabledNot(int enabled);
	List<Gateways> findAllBySystemIdAndEnabledNot(String systemId, int enabled);
	List<Gateways> findBySystemIdAndEnabledNotAndNetworkIdNot(String systemId, int enabled, int networkId);
	Gateways findBySystemIdAndEnabledNot(String systemId, int enabled);
	List<Gateways> findByMnoIdAndEnabledNot(int mnoId, int enabled);
	
	@Modifying
    @Transactional
    @Query("UPDATE Gateways sp SET sp.status = :status WHERE sp.networkId = :networkId")
    int saveStatusGateway(@Param("networkId") int networkId, @Param("status") String status);

	@Modifying
	@Transactional
	@Query("UPDATE Gateways sp SET sp.activeSessionsNumbers = CASE " +
			"WHEN (:nSessions = 1 AND sp.activeSessionsNumbers >= 0 AND sp.activeSessionsNumbers < sp.sessionsNumber) THEN sp.activeSessionsNumbers + 1 " +
			"WHEN (:nSessions = -1 AND sp.activeSessionsNumbers > 0) THEN sp.activeSessionsNumbers - 1 " +
			"WHEN (:nSessions = 0 ) THEN 0 " +
			"ELSE sp.activeSessionsNumbers END " +
			"WHERE sp.networkId = :networkId")
	int saveSessionsGateway(@Param("networkId") int networkId, @Param("nSessions") int nSessions);

	@Query("SELECT COUNT(gw) FROM Gateways gw WHERE enabled != 2 and LENGTH(gw.password) > :passwordLength")
	int countByPasswordLengthGreaterThan(int passwordLength);

	@Query("SELECT COUNT(gw) FROM Gateways gw WHERE enabled != 2 and LENGTH(gw.systemId) > :systemIdLength")
	int countBySystemIdLengthGreaterThan(int systemIdLength);
}
