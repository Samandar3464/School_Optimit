package com.example.exception;

public class AnnouncementAlreadyExistException extends RuntimeException{
    public AnnouncementAlreadyExistException(String name){
        super(name);
    }
}
