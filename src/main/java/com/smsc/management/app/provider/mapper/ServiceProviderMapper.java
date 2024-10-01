package com.smsc.management.app.provider.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.smsc.management.app.provider.dto.CallbackHeaderHttpDTO;
import com.smsc.management.app.provider.dto.ParseServiceProviderDTO;
import com.smsc.management.app.provider.dto.RedisServiceProviderDTO;
import com.smsc.management.app.provider.dto.ServiceProviderDTO;
import com.smsc.management.app.provider.model.entity.CallbackHeaderHttp;
import com.smsc.management.app.provider.model.entity.ServiceProvider;

@Mapper(componentModel = "spring")
public interface ServiceProviderMapper {
	ServiceProvider toEntity(ServiceProviderDTO serviceProviderDTO);
	ParseServiceProviderDTO toDTO(ServiceProvider serviceProviderEntity);
	List<ParseServiceProviderDTO> toDTO(List<ServiceProvider> serviceProviders);
	RedisServiceProviderDTO toServiceProviderDTO(ServiceProvider serviceProviderEntity);
	void updateEntityFromDTO(ServiceProviderDTO dto, @MappingTarget ServiceProvider entity);
	List<CallbackHeaderHttpDTO> toDTOCallbackHeader(List<CallbackHeaderHttp> headers);
	CallbackHeaderHttp toEntityCallbackHeader(CallbackHeaderHttpDTO headerDTO);
}
