package com.example.webRTC.exception;

public class RoomNotFoundException extends RuntimeException {
   public RoomNotFoundException(String message) {
       super(message);
   }
}