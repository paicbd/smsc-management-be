package com.smsc.management.app.settings.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.smsc.management.utils.StaticMethods;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Setter
@Getter
@Slf4j
public class GeneralSmscRetryDTO {
    private int id = 1;

    @Column(name = "first_retry_delay")
    private int firstRetryDelay = 0;

    @Column(name = "max_due_delay")
    private int maxDueDelay = 86400;

    @Column(name = "retry_delay_multiplier")
    private int retryDelayMultiplier = 2;

    @Override
    public String toString() {
        return StaticMethods.toJson(this);
    }
}
