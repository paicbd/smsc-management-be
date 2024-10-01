package com.smsc.management.app.provider.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smsc.management.app.provider.model.entity.CallbackHeaderHttp;
import org.springframework.transaction.annotation.Transactional;

public interface CallbackHeaderHttpRepository extends JpaRepository<CallbackHeaderHttp, Integer> {
	List<CallbackHeaderHttp> findByNetworkId(int id);
	@Transactional
	void deleteAllByNetworkId(int id);
}
