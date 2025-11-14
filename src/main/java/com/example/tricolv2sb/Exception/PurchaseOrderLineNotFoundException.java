package com.example.tricolv2sb.Exception;

public class PurchaseOrderLineNotFoundException extends RuntimeException {
    public PurchaseOrderLineNotFoundException(String message) {
        super(message);
    }
}
