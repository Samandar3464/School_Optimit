package com.example.exception;

public class CarNotFound extends RuntimeException {
    public CarNotFound(String name){
        super(name);
    }
}
