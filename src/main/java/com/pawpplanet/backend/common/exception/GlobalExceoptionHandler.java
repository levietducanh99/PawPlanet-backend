package com.pawpplanet.backend.common.exception;


import com.pawpplanet.backend.common.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceoptionHandler {

    @ExceptionHandler(value = RuntimeException.class)
    ResponseEntity handleRuntimeException(RuntimeException ex) {

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setStatusCode(1001);
        apiResponse.setMessage(ex.getMessage());

        // Log the exception or perform other actions as needed
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse> handleAppException(AppException ex) {

        ErrorCode errorCode = ex.getErrorCode();

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setStatusCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());

        // Log the exception or perform other actions as needed
        return ResponseEntity.badRequest().body(apiResponse);
    }

    /**
     * Handle validation errors (e.g., @Valid, @NotNull, @NotEmpty)
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        // Extract all field errors
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setStatusCode(400);
        apiResponse.setMessage("Validation failed");
        apiResponse.setResult(errors);

        return ResponseEntity.badRequest().body(apiResponse);
    }

}