package com.example.tricolv2sb.ExceptionHandler;

import com.example.tricolv2sb.Exception.*;
import com.example.tricolv2sb.Service.SupplierService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(SupplierService.class);

    @ExceptionHandler(SupplierAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleSupplierConflict(SupplierAlreadyExistsException e) {
        return buildErrorResponse(HttpStatus.CONFLICT, "Error adding new supplier : ", e);
    }

    @ExceptionHandler(SupplierNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleSupplierNotFound(SupplierNotFoundException e) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Error fetching supplier : ", e);
    }

    @ExceptionHandler(PurchaseOrderLineNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handlePurchaseOrderLineNotFound(PurchaseOrderLineNotFoundException e) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Error fetching purchase order line : ", e);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleProductNotFound(ProductNotFoundException e) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Error fetching product : ", e);
    }

    @ExceptionHandler(PurchaseOrderNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handlePurchaseOrderNotFound(PurchaseOrderNotFoundException e) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Error fetching purchase order : ", e);
    }

    @ExceptionHandler(GoodsIssueNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleGoodsIssueNotFound(GoodsIssueNotFoundException e) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Error fetching goods issue : ", e);
    }

    @ExceptionHandler(StockMovementNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleStockMovementNotFound(StockMovementNotFoundException e) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Error fetching stock movement : ", e);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalState(IllegalStateException e) {
        logger.error("Illegal state operation: {}", e.getMessage());

        Map<String, Object> body = new HashMap<>();
        body.put("Status", HttpStatus.CONFLICT.value());
        body.put("Message", e.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException e) {
        logger.error("Validation error occurred: {}", e.getMessage());

        Map<String, String> fieldErrors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage()
                                : "Invalid value",
                        (existing, replacement) -> existing));

        Map<String, Object> body = new HashMap<>();
        body.put("Status", HttpStatus.BAD_REQUEST.value());
        body.put("Message", "Validation failed");
        body.put("Errors", fieldErrors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception e) {
        logger.error("Unexpected error occurred: ", e);
        e.printStackTrace(); // Print full stack trace for debugging

        Map<String, Object> body = new HashMap<>();
        body.put("Status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("Message", e.getMessage());
        body.put("Error", e.getClass().getSimpleName());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String logPrefix, Exception e) {
        logger.error(logPrefix + e.getMessage());

        Map<String, Object> body = new HashMap<>();
        body.put("Status", status.value());
        body.put("Message", e.getMessage());

        return ResponseEntity.status(status).body(body);
    }

}
