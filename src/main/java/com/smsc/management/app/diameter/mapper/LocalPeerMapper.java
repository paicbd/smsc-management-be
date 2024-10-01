package com.smsc.management.app.diameter.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.smsc.management.app.diameter.dto.LocalPeerDTO;
import com.smsc.management.app.diameter.model.entity.LocalPeer;

@Mapper(componentModel = "spring")
public interface LocalPeerMapper {
	LocalPeer toEntity(LocalPeerDTO dto);
	void updateEntityFromDTO(LocalPeerDTO dto, @MappingTarget LocalPeer entity);
	LocalPeerDTO toDTO(LocalPeer entity);
	List<LocalPeerDTO> toDTOList(List<LocalPeer> entities);
}
