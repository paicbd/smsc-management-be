package com.smsc.management.app.ss7.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.smsc.management.app.ss7.dto.M3uaApplicationServerDTO;
import com.smsc.management.app.ss7.dto.M3uaAssociationsDTO;
import com.smsc.management.app.ss7.dto.M3uaDTO;
import com.smsc.management.app.ss7.dto.M3uaRoutesDTO;
import com.smsc.management.app.ss7.dto.M3uaSocketsDTO;
import com.smsc.management.app.ss7.model.entity.M3ua;
import com.smsc.management.app.ss7.model.entity.M3uaApplicationServer;
import com.smsc.management.app.ss7.model.entity.M3uaAssociations;
import com.smsc.management.app.ss7.model.entity.M3uaRoutes;
import com.smsc.management.app.ss7.model.entity.M3uaSockets;

@Mapper(componentModel = "spring")
public interface M3uaMapper {
	M3ua toEntity(M3uaDTO m3ua);
	M3uaDTO toDTO(M3ua m3uaEntity);
	List<M3ua> toEntityList(List<M3uaDTO> m3uaDTOList);
	List<M3uaDTO> toDTOList(List<M3ua> m3uaList);
	
	// servers
	M3uaSockets toEntityServer(M3uaSocketsDTO m3uaServer);
	M3uaSocketsDTO toDTOServer(M3uaSockets m3uaServerEntity);
	List<M3uaSockets> toEntityServerList(List<M3uaSocketsDTO> m3uaServer);
	List<M3uaSocketsDTO> toDTOServerList(List<M3uaSockets> m3uaServerEntity);
	
	// associations
	M3uaAssociations toEntityAssociation(M3uaAssociationsDTO m3uaAssociationDTO);
	M3uaAssociationsDTO toDTOAssociation(M3uaAssociations m3uaAssociationEntity);
	List<M3uaAssociations> toEntityAssociationList(List<M3uaAssociationsDTO> m3uaAssociationDTO);
	List<M3uaAssociationsDTO> toDTOAssociationList(List<M3uaAssociations> m3uaAssociationEntity);
	
	// application servers
	M3uaApplicationServer toEntityAppServer(M3uaApplicationServerDTO m3uaASDTO);
	M3uaApplicationServerDTO toDTOAppServer(M3uaApplicationServer m3uaAS);
	List<M3uaApplicationServer> toEntityAppServerList(List<M3uaApplicationServerDTO> m3uaASDTO);
	List<M3uaApplicationServerDTO> toDTOAppServerList(List<M3uaApplicationServer> m3uaAS);
	
	// routes
	M3uaRoutes toEntityRoutes(M3uaRoutesDTO m3uaRouteDTO);
	M3uaRoutesDTO toDTORoutes(M3uaRoutes m3uaRoute);
	List<M3uaRoutes> toEntityRoutesList(List<M3uaRoutesDTO> m3uaRouteDTO);
	List<M3uaRoutesDTO> toDTORoutesList(List<M3uaRoutes> m3uaRoute);
}