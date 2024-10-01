package com.smsc.management.app.diameter.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smsc.management.app.diameter.model.entity.Peers;
import java.util.List;


public interface PeersRepository extends JpaRepository<Peers, Integer> {
	Peers findById(int id);
	
	List<Peers> findByDiameterId(int diameterId);
}
