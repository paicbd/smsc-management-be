package com.smsc.management.app.diameter.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.smsc.management.app.diameter.dto.RealmsDTO;
import com.smsc.management.app.diameter.model.entity.Realms;

@Mapper(componentModel = "spring")
public interface RealmsMapper {
	Realms toEntity(RealmsDTO dto);
	void updateEntityFromDTO(RealmsDTO dto, @MappingTarget Realms entity);
	RealmsDTO toDTO(Realms entity);
	List<RealmsDTO> toDTOList(List<Realms> entities);
}
