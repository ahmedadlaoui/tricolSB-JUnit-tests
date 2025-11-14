package com.example.tricolv2sb.Exception;

public class SupplierAlreadyExistsException extends RuntimeException {

    public SupplierAlreadyExistsException() {
        super();
    }

    public SupplierAlreadyExistsException(String message) {
        super(message);
    }

    public SupplierAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public SupplierAlreadyExistsException(Throwable cause) {
        super(cause);
    }
}
