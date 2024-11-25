package com.example.webRTC.exception;

public class RoomFullException extends RuntimeException {
    public RoomFullException(String message) {
        super(message);
    }
}