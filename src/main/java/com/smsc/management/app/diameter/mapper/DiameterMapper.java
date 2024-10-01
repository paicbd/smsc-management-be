package com.smsc.management.app.diameter.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.smsc.management.app.diameter.dto.DiameterDTO;
import com.smsc.management.app.diameter.model.entity.Diameter;

@Mapper(componentModel = "spring")
public interface DiameterMapper {
	Diameter toEntity(DiameterDTO diameterDto);
	void updateEntityFromDTO(DiameterDTO dto, @MappingTarget Diameter entity);
	DiameterDTO toDTO(Diameter diameterEntity);
	List<DiameterDTO> toDTOList(List<Diameter> diameterEntity);
}
