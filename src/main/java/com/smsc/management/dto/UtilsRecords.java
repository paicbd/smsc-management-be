package com.smsc.management.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UtilsRecords {
    public record SystemIdInputParameter(@JsonProperty("system_id") String systemId) {
    }
}
