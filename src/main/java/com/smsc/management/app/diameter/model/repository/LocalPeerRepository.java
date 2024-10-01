package com.smsc.management.app.diameter.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smsc.management.app.diameter.model.entity.LocalPeer;


public interface LocalPeerRepository extends JpaRepository<LocalPeer, Integer> {
	LocalPeer findById(int id);
	
	LocalPeer findByDiameterId(int diameterId);
}
