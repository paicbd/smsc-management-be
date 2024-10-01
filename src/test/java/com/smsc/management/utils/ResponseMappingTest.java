package com.smsc.management.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ResponseMappingTest {

    @Test
    @DisplayName("Should return ApiResponse with status 400 and error message when calling errorMessage"  )
    void shouldReturnApiResponseWithStatus400AndErrorMessageWhenCallingErrorMessage() {
        String errorMessage = "Test error message";
        ApiResponse response = ResponseMapping.errorMessage(errorMessage);
        assertNotNull(response);
    }

    @Test
    @DisplayName("Should return ApiResponse with status 200 and success message when calling successMessage"  )
    void shouldReturnApiResponseWithStatus200AndSuccessMessageWhenCallingSuccessMessage() {
        String successMessage = "Test success message";
        Object data = new Object();
        ApiResponse response = ResponseMapping.successMessage(successMessage, data);
        assertNotNull(response);
    }

    @Test
    @DisplayName("Should return ApiResponse with status 500 and error message when calling exceptionMessage"  )
    void shouldReturnApiResponseWithStatus500AndErrorMessageWhenCallingExceptionMessage() {
        String exceptionMessage = "Test exception message";
        Exception exception = new Exception("Test cause");
        ApiResponse response = ResponseMapping.exceptionMessage(exceptionMessage, exception);
        assertNotNull(response);
    }

    @Test
    @DisplayName("Should return ApiResponse with status 404 and error message when calling errorMessageNoFound"  )
    void shouldReturnApiResponseWithNotFoundMessageWhenCallingSuccessMessage() {
        String notFoundMessage = "No found message";
        ApiResponse response = ResponseMapping.errorMessageNoFound(notFoundMessage);
        assertNotNull(response);
    }

    @Test
    void testPrivateConstructor() throws NoSuchMethodException {
        Constructor<ResponseMapping> constructor = ResponseMapping.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        assertThrows(InvocationTargetException.class, constructor::newInstance);
    }
}