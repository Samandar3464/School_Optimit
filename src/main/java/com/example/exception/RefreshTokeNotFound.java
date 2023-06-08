package com.example.exception;

public class RefreshTokeNotFound extends RuntimeException {
    public RefreshTokeNotFound(String reFreshTokenNotFound) {
        super(reFreshTokenNotFound);
    }
}
