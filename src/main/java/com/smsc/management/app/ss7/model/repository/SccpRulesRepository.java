package com.smsc.management.app.ss7.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smsc.management.app.ss7.dto.SccpRulesDTO;
import com.smsc.management.app.ss7.model.entity.SccpRules;

public interface SccpRulesRepository extends JpaRepository<SccpRules, Integer> {
	SccpRules findById(int id);
	
	@Query("SELECT new com.smsc.management.app.ss7.dto.SccpRulesDTO(sr.id, sr.name, sr.mask, sr.addressIndicator, "
	        + "sr.pointCode, sr.subsystemNumber, sr.gtIndicator, sr.translationType, sr.numberingPlanId, "
	        + "sr.natureOfAddressId, sr.globalTittleDigits, sr.ruleTypeId, sr.primaryAddressId, sr.secondaryAddressId, "
	        + "sr.loadSharingAlgorithmId, sr.newCallingPartyAddress, sr.originationTypeId, sr.callingAddressIndicator, "
	        + "sr.callingPointCode, sr.callingSubsystemNumber, sr.callingTranslatorType, sr.callingNumberingPlanId, "
	        + "sr.callingNatureOfAddressId, sr.callingGtIndicator, sr.callingGlobalTittleDigits) " +
	        "FROM SccpRules sr " +
	        "LEFT JOIN sr.primaryAddress pa " +
	        "LEFT JOIN sr.secondaryAddress sa " +
	        "WHERE (pa.ss7SccpId = :sccpId OR sa.ss7SccpId = :sccpId) " +
	        "ORDER BY sr.id")
	List<SccpRulesDTO> fetchSccpRules(@Param("sccpId") int sccpId);
}
