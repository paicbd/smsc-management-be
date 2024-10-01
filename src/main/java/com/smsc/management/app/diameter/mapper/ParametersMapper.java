package com.smsc.management.app.diameter.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.smsc.management.app.diameter.dto.ParametersDTO;
import com.smsc.management.app.diameter.model.entity.Parameters;

@Mapper(componentModel = "spring")
public interface ParametersMapper {
	Parameters toEntity(ParametersDTO dto);
	void updateEntityFromDTO(ParametersDTO dto, @MappingTarget Parameters entity);
	ParametersDTO toDTO(Parameters entity);
	List<ParametersDTO> toDTOList(List<Parameters> entities);
}
