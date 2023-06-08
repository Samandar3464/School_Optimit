package com.example.exception;

public class AnnouncementNotFoundException extends RuntimeException{
    public AnnouncementNotFoundException(String name){
        super(name);
    }
}
