package com.smsc.management.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class StaticMethodsTest {
    @Test
    void fromJsonSuccess() {
        GlobalRecords.SystemIdInputParameter systemIdInputParameter = StaticMethods.fromJson("{\"system_id\":\"system_id\"}", GlobalRecords.SystemIdInputParameter.class);
        assertNotNull(systemIdInputParameter);
    }

    @Test
    void fromJsonFailure() {
        GlobalRecords.SystemIdInputParameter systemIdInputParameter = StaticMethods.fromJson("{\"system_id\":\"system_id\"", GlobalRecords.SystemIdInputParameter.class);
        assertNull(systemIdInputParameter);
    }

    @Test
    void toJsonSuccess() {
        String json = StaticMethods.toJson(new GlobalRecords.SystemIdInputParameter("system_id"));
        assertNotNull(json);
    }

    @Test
    void toJsonFailure() {
        String json = StaticMethods.toJson(new Object());
        assertNull(json);
    }

    @Test
    void testPrivateConstructor() throws NoSuchMethodException {
        Constructor<StaticMethods> constructor = StaticMethods.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        assertThrows(InvocationTargetException.class, constructor::newInstance);
    }
}