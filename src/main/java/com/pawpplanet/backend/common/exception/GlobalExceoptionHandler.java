package com.pawpplanet.backend.common.exception;


import com.pawpplanet.backend.common.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

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



}
