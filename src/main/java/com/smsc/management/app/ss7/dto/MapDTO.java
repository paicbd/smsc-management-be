package com.smsc.management.app.ss7.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MapDTO {
    private int id;

    @JsonProperty("network_id")
    private int  networkId;

    @JsonProperty("sri_service_op_code")
    private int sriServiceOpCode;

    @JsonProperty("forward_sm_service_op_code")
    private int forwardSmServiceOpCode;
}
