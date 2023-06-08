package com.example.exception;

public class PermissionNotFound extends RuntimeException {
    public PermissionNotFound() {
    }

    public PermissionNotFound(String message) {
        super(message);
    }

    public PermissionNotFound(String message, Throwable cause) {
        super(message, cause);
    }
}
