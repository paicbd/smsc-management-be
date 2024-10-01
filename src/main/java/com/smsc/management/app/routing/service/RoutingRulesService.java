package com.smsc.management.app.routing.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.smsc.management.utils.AppProperties;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.smsc.management.app.routing.dto.NetworksToRoutingRulesDTO;
import com.smsc.management.app.routing.dto.RedisRoutingRulesDTO;
import com.smsc.management.utils.ApiResponse;
import com.smsc.management.app.routing.dto.RoutingRulesDTO;
import com.smsc.management.app.routing.dto.RoutingRulesDestinationDTO;
import com.smsc.management.app.routing.model.entity.RoutingRules;
import com.smsc.management.app.routing.model.entity.RoutingRulesDestination;
import com.smsc.management.app.sequence.SequenceNetworksId;
import com.smsc.management.exception.InvalidStructureException;
import com.smsc.management.app.routing.mapper.RoutingRulesMapper;
import com.smsc.management.app.routing.model.repository.RoutingRulesDestinationRepository;
import com.smsc.management.app.routing.model.repository.RoutingRulesRepository;
import com.smsc.management.app.sequence.SequenceNetworksIdRepository;
import com.smsc.management.utils.Constants;
import com.smsc.management.utils.ResponseMapping;
import com.smsc.management.utils.UtilsBase;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * Service class for processing routing rules and their destinations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoutingRulesService {
    
    private final RoutingRulesRepository routingRuleRepo;
    private final RoutingRulesDestinationRepository routingRulesDestinationRepo;
    private final SequenceNetworksIdRepository sequenceNetworkRepo;
    private final RoutingRulesMapper routingRulesMapper;
    private final UtilsBase utilsBase;
    private final AppProperties appProperties;

    /**
     * Retrieves all routing rules along with their destinations.
     *
     * @return A ResponseDTO containing the list of routing rules and their destinations.
     */
    public ApiResponse getRoutingRules() {
        try {
            List<RoutingRulesDTO> routingRulesDTOs = routingRuleRepo.findByRoutingRulesList();

            routingRulesDTOs.forEach(dto -> {
                List<RoutingRulesDestinationDTO> destinationsDTO = routingRulesDestinationRepo.findByRoutingRulesIdList(dto.getId());
                dto.setDestination(destinationsDTO);
            });

            return ResponseMapping.successMessage("get routing rules request success", routingRulesDTOs);
        } catch (Exception e) {
            log.error("Get routing rules request with error: {}", e.getMessage());
            return ResponseMapping.exceptionMessage("Get routing rules request with error", e);
        }
    }

    /**
     * Creates a new routing rule along with its destinations.
     *
     * @param newRouting The new routing rule to be created.
     * @return A ResponseDTO indicating the success or failure of the operation.
     */
    @Transactional
    public ApiResponse create(RoutingRulesDTO newRouting) {
        int rulesId = 0;
        try {
            RoutingRules routingRules = new RoutingRules();
            List<RoutingRulesDestination> routingRulesDestinationArray = new ArrayList<>();
            List<RoutingRulesDestinationDTO> destinations = newRouting.getDestination();

            if (destinations.isEmpty()) {
                throw new InvalidStructureException("At least one destination is required.");
            }
            utilsBase.validateDestinationRules(destinations);
            utilsBase.validateNetworksParameter(newRouting);

            routingRulesMapper.updateEntityFromDTO(newRouting, routingRules);
            var resultInserted = routingRuleRepo.save(routingRules);
            rulesId = resultInserted.getId();

            for (RoutingRulesDestinationDTO routing : destinations) {
                RoutingRulesDestination routingRulesDestination = new RoutingRulesDestination();
                SequenceNetworksId seqNet = sequenceNetworkRepo.findById(routing.getNetworkId());

                routingRulesDestination.setNetworkId(routing.getNetworkId());
                routingRulesDestination.setPriority(routing.getPriority());
                routingRulesDestination.setRoutingRulesId(rulesId);
                routingRulesDestination.setNetworkType(seqNet.getNetworkType());

                routingRulesDestinationArray.add(routingRulesDestination);
            }
            routingRulesDestinationRepo.saveAll(routingRulesDestinationArray);

            // prepare data response
            List<RoutingRulesDestinationDTO> destinationsDTO = routingRulesDestinationRepo.findByRoutingRulesIdList(rulesId);
            newRouting.setDestination(destinationsDTO);
            newRouting.setId(rulesId);

            if (this.socketAndRedisAction(newRouting.getOriginNetworkId())) {
                log.info("Routing rules stored in Redis and socket success -> {}", newRouting);
            }

            return ResponseMapping.successMessage("Routing rule added successfully.", newRouting);
        } catch (DataIntegrityViolationException e) {
            if (rulesId > 0) {
                manualRollback(rulesId);
            }
            log.error("New routing rules request with DataIntegrityViolationException: {}", e.getMessage());
            throw e;
        } catch (InvalidStructureException e) {
            log.error("New routing rules request with InvalidStructureException: {}", e.getMessage());
            return e.exceptionMessage("New routing rules request with error", e);
        } catch (Exception e) {
            log.error("New routing rules request with error: {}", e.getMessage());
            return ResponseMapping.exceptionMessage("New routing rules request with error", e);
        }
    }

    /**
     * Updates an existing routing rule along with its destinations.
     *
     * @param id              The ID of the routing rule to be updated.
     * @param routingRulesDTO The updated routing rule information.
     * @return A ResponseDTO indicating the success or failure of the operation.
     */
    @Transactional
    public ApiResponse update(int id, RoutingRulesDTO routingRulesDTO) {
        try {
            // validate destinations rules
            List<RoutingRulesDestinationDTO> destinations = routingRulesDTO.getDestination();
            if (destinations.isEmpty()) {
                throw new InvalidStructureException("At least one destination is required.");
            }
            utilsBase.validateDestinationRules(destinations);
            utilsBase.validateNetworksParameter(routingRulesDTO);

            // finding routing rule
            RoutingRules routingRules = routingRuleRepo.findById(id);
            if (Objects.isNull(routingRules)) {
                return ResponseMapping.errorMessageNoFound("Routing rules with ID= " + id + " was not found.");
            }
            int previousNetworkId = routingRules.getOriginNetworkId();

            for (RoutingRulesDestinationDTO destination : destinations) {
                RoutingRulesDestination routingRulesDestination = new RoutingRulesDestination();
                SequenceNetworksId seqNet = sequenceNetworkRepo.findById(destination.getNetworkId());

                if (destination.getAction() != 2) {
                    routingRulesDestination = routingRulesDestinationRepo.findById(destination.getId());
                } else {
                    routingRulesDestination.setPriority(destination.getPriority());
                    routingRulesDestination.setNetworkId(destination.getNetworkId());
                    routingRulesDestination.setNetworkType(seqNet.getNetworkType());
                }

                switch (destination.getAction()) {
                    case 0:
                        routingRulesDestinationRepo.save(routingRulesDestination);
                        break;
                    case 1:
                        routingRulesDestinationRepo.delete(routingRulesDestination);
                        break;
                    case 2:
                        routingRulesDestination.setRoutingRulesId(id);
                        routingRulesDestinationRepo.save(routingRulesDestination);
                        break;
                    default:
                        throw new InvalidStructureException("Only values 0 (update), 1 (delete) and 2 (new) are allowed in the action field");
                }
            }
            routingRulesDTO.setId(id);
            routingRulesMapper.updateEntityFromDTO(routingRulesDTO, routingRules);
            var resultRoutingRule = routingRuleRepo.save(routingRules);

            // mapping dto response
            routingRulesMapper.DTOfromEntity(resultRoutingRule, routingRulesDTO);
            List<RoutingRulesDestinationDTO> destinationsDTO = routingRulesDestinationRepo.findByRoutingRulesIdList(id);
            routingRulesDTO.setDestination(destinationsDTO);

            if (this.socketAndRedisAction(routingRulesDTO.getOriginNetworkId())) {
                log.info("Routing rules updated in Redis and socket success -> {}", routingRulesDTO);

                if (previousNetworkId != routingRulesDTO.getOriginNetworkId() && this.socketAndRedisAction(previousNetworkId)) {
                    log.info("Routing rules updated in Redis and socket success with previous originNetworkId = {}", previousNetworkId);
                }
            }

            return ResponseMapping.successMessage("Routing rule updated successfully.", routingRulesDTO);
        } catch (DataIntegrityViolationException e) {
            log.error("Routing rules request to update with DataIntegrityViolationException: {}", e.getMessage());
            throw e;
        } catch (InvalidStructureException e) {
            log.error("Routing rules request to update with InvalidStructureException: {}", e.getMessage());
            return e.exceptionMessage("Routing rules request to update with error", e);
        } catch (Exception e) {
            log.error("Routing rules request with error: {}", e.getMessage());
            return ResponseMapping.exceptionMessage("Routing rules request with error", e);
        }
    }

    /**
     * Deletes a routing rule by its ID along with its destinations.
     *
     * @param id The ID of the routing rule to be deleted.
     * @return A ResponseDTO indicating the success or failure of the operation.
     */
    @Transactional
    public ApiResponse delete(int id) {
        try {
            RoutingRules routingRule = routingRuleRepo.findById(id);
            if (routingRule != null) {
                // routing rules data to response
                List<RoutingRulesDestinationDTO> routingRulesDestinationDTO = routingRulesDestinationRepo.findByRoutingRulesIdList(id);
                RoutingRulesDTO routingRulesDTO = new RoutingRulesDTO();
                routingRulesMapper.DTOfromEntity(routingRule, routingRulesDTO);
                routingRulesDTO.setDestination(routingRulesDestinationDTO);

                // deleting data
                List<RoutingRulesDestination> routingRuleDestinationDeleted = routingRulesDestinationRepo.findByRoutingRulesId(id);
                routingRulesDestinationRepo.deleteAll(routingRuleDestinationDeleted);
                routingRuleRepo.delete(routingRule);

                if (this.socketAndRedisAction(routingRule.getOriginNetworkId())) {
                    log.info("Routing rules deleted in Redis and socket success -> {}", routingRulesDTO);
                }

                return ResponseMapping.successMessage("Routing rules deleted successful.", routingRulesDTO);
            }

            return ResponseMapping.errorMessageNoFound("Routing rules was not found.");
        } catch (Exception e) {
            log.error("Routing rules request with error in delete(): {}", e.getMessage());
            return ResponseMapping.exceptionMessage("Routing rules request was end with error", e);
        }
    }

    /**
     * Performs a manual rollback of transactions in case of an error during the creation of a routing rule.
     *
     * @param routingRulesId The ID of the routing rule to perform the rollback.
     */
    public void manualRollback(int routingRulesId) {
        try {
            log.info("applying rollback to routing_rules_destination table");
            List<RoutingRulesDestination> routingDestinations = routingRulesDestinationRepo.findByRoutingRulesId(routingRulesId);
            routingRulesDestinationRepo.deleteAll(routingDestinations);

            log.info("applying rollback to routing_rules table");
            RoutingRules routingRule = routingRuleRepo.findById(routingRulesId);
            routingRuleRepo.delete(routingRule);
        } catch (Exception e) {
            log.error("applying rollback ended with error: {}", e.getMessage());
        }
    }

    public ApiResponse getNetworks() {
        try {
            List<NetworksToRoutingRulesDTO> networks = routingRuleRepo.findGatewayNamesAndIds();
            return ResponseMapping.successMessage("Get networks successful", networks);
        } catch (Exception e) {
            log.error("Error to get networks list: {}", e.getMessage());
            return ResponseMapping.exceptionMessage("Error to get networks", e);
        }
    }

    public boolean socketAndRedisAction(int originNetWorkId) {
        try {
            String originProtocol = routingRuleRepo.findOriginProtocol(originNetWorkId);
            List<RedisRoutingRulesDTO> routingRules = utilsBase.getRoutingRules(originNetWorkId, originProtocol);

            if (routingRules.isEmpty()) {
                utilsBase.removeInRedis(appProperties.getHashRoutingRules(), Integer.toString(originNetWorkId));
                utilsBase.sendNotificationSocket(Constants.DELETE_ROUTING_RULES_ENDPOINT, Integer.toString(originNetWorkId));
                return true;
            }

            utilsBase.storeInRedis(appProperties.getHashRoutingRules(), Integer.toString(originNetWorkId), routingRules.toString());
            utilsBase.sendNotificationSocket(Constants.UPDATE_ROUTING_RULES_ENDPOINT, Integer.toString(originNetWorkId));

            return true;
        } catch (Exception e) {
            log.error("Error to create object in Redis and sent socket notification -> {}", e.getMessage());
        }
        return false;
    }
}
