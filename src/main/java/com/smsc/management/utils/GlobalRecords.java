package com.smsc.management.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.paicbd.smsc.utils.Generated;

@Generated
public class GlobalRecords {
    public record SystemIdInputParameter(@JsonProperty("system_id") String systemId) {
    }
}
