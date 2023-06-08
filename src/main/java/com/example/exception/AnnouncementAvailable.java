package com.example.exception;

public class AnnouncementAvailable extends RuntimeException {
     public AnnouncementAvailable(String s) {
          super(s);
     }
}
