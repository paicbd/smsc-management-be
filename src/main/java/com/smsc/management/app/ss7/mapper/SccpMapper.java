package com.smsc.management.app.ss7.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.smsc.management.app.ss7.dto.SccpAddressesDTO;
import com.smsc.management.app.ss7.dto.SccpDTO;
import com.smsc.management.app.ss7.dto.SccpMtp3DestinationsDTO;
import com.smsc.management.app.ss7.dto.SccpRemoteResourcesDTO;
import com.smsc.management.app.ss7.dto.SccpRulesDTO;
import com.smsc.management.app.ss7.dto.SccpServiceAccessPointsDTO;
import com.smsc.management.app.ss7.model.entity.Sccp;
import com.smsc.management.app.ss7.model.entity.SccpAddresses;
import com.smsc.management.app.ss7.model.entity.SccpMtp3Destinations;
import com.smsc.management.app.ss7.model.entity.SccpRemoteResources;
import com.smsc.management.app.ss7.model.entity.SccpRules;
import com.smsc.management.app.ss7.model.entity.SccpServiceAccessPoints;

@Mapper(componentModel = "spring")
public interface SccpMapper {
	Sccp toEntity(SccpDTO sccpDto);
	SccpDTO toDTO(Sccp sccpEntity);
	List<Sccp> toEntity(List<SccpDTO> sccpDtoList);
	List<SccpDTO> toDTO(List<Sccp> sccpEntity);
	
	SccpRemoteResources toEntityRemoteResources(SccpRemoteResourcesDTO sccpRemoteResource);
	SccpRemoteResourcesDTO toDTORemoteResources(SccpRemoteResources sccpRemoteResourceEntity);
	List<SccpRemoteResources> toEntityRemoteResources(List<SccpRemoteResourcesDTO> sccpRemoteResources);
	List<SccpRemoteResourcesDTO> toDTORemoteResources(List<SccpRemoteResources> sccpRemoteResourcesEntity);
	
	SccpServiceAccessPoints toEntitySap(SccpServiceAccessPointsDTO sccpSapDTO);
	SccpServiceAccessPointsDTO toDTOSap(SccpServiceAccessPoints sccpSapEntity);
	List<SccpServiceAccessPoints> toEntitySap(List<SccpServiceAccessPointsDTO> sccpSapDTO);
	List<SccpServiceAccessPointsDTO> toDTOSap(List<SccpServiceAccessPoints> sccpSapEntity);
	
	SccpMtp3Destinations toEntityMTP3(SccpMtp3DestinationsDTO sccpMtpDest);
	SccpMtp3DestinationsDTO toDTOMTP3(SccpMtp3Destinations sccpMtpDestEntity);
	List<SccpMtp3Destinations> toEntityMTP3(List<SccpMtp3DestinationsDTO> sccpMtpDest);
	List<SccpMtp3DestinationsDTO> toDTOMTP3(List<SccpMtp3Destinations> sccpMtpDestEntity);
	
	SccpAddresses toEntityAddress(SccpAddressesDTO addressDTO);
	SccpAddressesDTO toDTOAddress(SccpAddresses addressEntity);
	List<SccpAddresses> toEntityAddress(List<SccpAddressesDTO> addressDTO);
	List<SccpAddressesDTO> toDTOAddress(List<SccpAddresses> addressEntity);
	
	SccpRules toEntityRules(SccpRulesDTO sccpRulesDTO);
	SccpRulesDTO toDTORules(SccpRules sccpRulesEntity);
	List<SccpRules> toEntityRules(List<SccpRulesDTO> sccpRulesDTO);
	List<SccpRulesDTO> toDTORules(List<SccpRules> sccpRulesEntity);
}
