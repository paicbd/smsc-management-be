package com.smsc.management.exception;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.NonNull;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.smsc.management.utils.ApiResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    static final String ERROR = "error";

    @ExceptionHandler(Exception.class)
    public ApiResponse handleException(Exception e) {
        return new ApiResponse(400, ERROR, e.getMessage(), null);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status, @NonNull WebRequest request) {
        ApiResponse apiResponse = new ApiResponse(400, ERROR, ex.getMessage(), null);
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  @NonNull HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream().map(x -> "Fatal error: Column '" + x.getField() + "' => " + Objects.requireNonNull(x.getDefaultMessage()).toLowerCase(Locale.ROOT))
                .collect(Collectors.joining(", "));
        ApiResponse apiResponse = new ApiResponse(400, ERROR, message, null);
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public final ResponseEntity<Map<String, Object>> handleDataIntegrityViolationException(DataIntegrityViolationException e, Exception ex, WebRequest request) {
        log.error("DataIntegrityViolationException: {}", e.getMessage());
        Map<String, Object> body = new LinkedHashMap<>();

        body.put("status", 400);
        body.put("message", ERROR);

        String message = ex.getCause().getCause().getMessage();
        message = message.substring(message.indexOf("Detail:"));
        message = message.replace("\"", "'");

        body.put("comment", "Error " + message);
        body.put("data", null);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}
