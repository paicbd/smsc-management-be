package com.smsc.management.app.diameter.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smsc.management.app.diameter.dto.RealmsDTO;
import com.smsc.management.app.diameter.model.entity.Realms;
import java.util.List;


public interface RealmsRepository extends JpaRepository<Realms, Integer> {
	Realms findById(int id);
	List<Realms> findByPeerId(int peerId);
	
	@Query("SELECT new com.smsc.management.app.diameter.dto.RealmsDTO(r.id, r.name, r.domain, r.localAction, r.dynamic, r.expTime, r.peerId, r.applicationId) " +
	           "FROM Realms r " +
	           "JOIN r.peers p " +
	           "WHERE p.diameterId = :diameterId " +
	           "ORDER BY r.id")
	List<RealmsDTO> fetchRealms(@Param("diameterId") int diameterId);
}
