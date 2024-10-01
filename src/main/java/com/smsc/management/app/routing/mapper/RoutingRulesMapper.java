package com.smsc.management.app.routing.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.smsc.management.app.routing.dto.RedisRoutingRulesDTO;
import com.smsc.management.app.routing.dto.RoutingRulesDTO;
import com.smsc.management.app.routing.dto.RoutingRulesDestinationDTO;
import com.smsc.management.app.routing.model.entity.RoutingRules;
import com.smsc.management.app.routing.model.entity.RoutingRulesDestination;

@Mapper(componentModel = "spring")
public interface RoutingRulesMapper {
	List<RoutingRulesDTO> toListDTO(List<RoutingRules> entities);
	List<RoutingRulesDestinationDTO> toListDestDTO(List<RoutingRulesDestination> entities);
	List<RoutingRulesDestination> toEntity(List<RoutingRulesDestinationDTO> dtos);
	RoutingRulesDestinationDTO toDTO(RoutingRulesDestination entity);
	RedisRoutingRulesDTO toRedisDTO(RoutingRules entities);
	
	void updateEntityFromDTO(RoutingRulesDTO dto, @MappingTarget RoutingRules entity);
	void DTOfromEntity(RoutingRules entity, @MappingTarget RoutingRulesDTO dto);
	void DTOfromEntity(List<RoutingRulesDestination> entities, @MappingTarget List<RoutingRulesDestinationDTO> dtos);
}
