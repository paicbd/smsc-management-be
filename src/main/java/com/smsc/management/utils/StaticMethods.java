package com.smsc.management.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StaticMethods {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private StaticMethods() {
        throw new IllegalStateException("Utility class");
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            log.error("Error while converting json to {} object", clazz.getName(), e);
            return null;
        }
    }

    public static String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            log.error("Error while converting {} object to json", object.getClass().getName(), e);
            return null;
        }
    }
}
