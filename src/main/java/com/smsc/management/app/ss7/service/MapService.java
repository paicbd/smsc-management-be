package com.smsc.management.app.ss7.service;

import com.smsc.management.utils.ApiResponse;
import com.smsc.management.app.ss7.dto.MapDTO;
import com.smsc.management.app.ss7.model.entity.Map;
import com.smsc.management.app.ss7.mapper.MapMapper;
import com.smsc.management.app.ss7.model.repository.MapRepository;
import com.smsc.management.utils.ResponseMapping;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class MapService {

    private final MapRepository mapRepository;
    private final MapMapper mapMapper;

    /**
     * Retrieves Map configuration by network ID.
     *
     * @param networkId The network ID for which ss7 gateway map configuration is being retrieved.
     * @return A ResponseDTO object containing the request response.
     */
    public ApiResponse getMapByNetworkId(int networkId) {
        try {
            Map map = mapRepository.findByNetworkId(networkId);
            if (map != null) {
                return ResponseMapping.successMessage("get map request successful.", mapMapper.toDTO(map));
            }
            return ResponseMapping.errorMessageNoFound("No Map configuration found for network_id= " + networkId);
        } catch (Exception e) {
            log.error("Map request with error: {}", e.getMessage());
            return ResponseMapping.exceptionMessage("Map was end with error", e);
        }
    }

    /**
     * Creates a new Map configuration.
     *
     * @param newMap The MapDTO object containing details of the new Map configuration.
     * @return A ResponseDTO object containing the request response.
     */
    public ApiResponse create(MapDTO newMap) {
        try {
            if (Objects.isNull(newMap)) {
                throw  new IllegalArgumentException("New map objects invalid");
            }

            Map mapEntity = mapMapper.toEntity(newMap);
            var result = mapRepository.save(mapEntity);
            return ResponseMapping.successMessage("map created successful.", mapMapper.toDTO(result));
        } catch (Exception e) {
            log.error("New Map request with error: {}", e.getMessage());
            return ResponseMapping.exceptionMessage("New Map was end with error", e);
        }
    }

    /**
     * Updates an existing Map configuration.
     *
     * @param id The ID of the Tcap configuration to be updated.
     * @param map The MapDTO object containing the new details of the Map configuration.
     * @return A ResponseDTO object containing the request response.
     */
    public ApiResponse update(int id, MapDTO map) {
        try {
            Map currentMap = mapRepository.findById(id);
            if (currentMap != null) {
                currentMap = mapMapper.toEntity(map);
                Map result = mapRepository.save(currentMap);
                return ResponseMapping.successMessage("map update successful.", mapMapper.toDTO(result));
            }
            return ResponseMapping.errorMessageNoFound("map configuration with id = " + id + " was not found.");
        } catch (Exception e) {
            log.error("Update Map request with error: {}", e.getMessage());
            return ResponseMapping.exceptionMessage("Update Map was end with error", e);
        }
    }

    /**
     * Deletes an existing Map configuration by its ID.
     *
     * @param id The ID of the Map configuration to be deleted.
     * @return A ResponseDTO object containing the request response.
     */
    public ApiResponse delete(int id) {
        try {
            Map map = mapRepository.findByNetworkId(id);
            if (map != null) {
                mapRepository.delete(map);
                return ResponseMapping.successMessage("map deleted successful.", null);
            }
            return ResponseMapping.errorMessageNoFound("map configuration with id = " + id + " was not found.");
        } catch (Exception e) {
            log.error("Delete Map request with error: {}", e.getMessage());
            return ResponseMapping.exceptionMessage("Delete Map was end with error", e);
        }
    }
}
