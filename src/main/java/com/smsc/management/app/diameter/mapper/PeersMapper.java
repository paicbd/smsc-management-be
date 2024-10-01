package com.smsc.management.app.diameter.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.smsc.management.app.diameter.dto.PeersDTO;
import com.smsc.management.app.diameter.model.entity.Peers;

@Mapper(componentModel = "spring")
public interface PeersMapper {
	Peers toEntity(PeersDTO dto);
	void updateEntityFromDTO(PeersDTO dto, @MappingTarget Peers entity);
	PeersDTO toDTO(Peers entity);
	List<PeersDTO> toDTOList(List<Peers> entities);
}
