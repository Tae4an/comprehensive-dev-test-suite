package com.example.webRTC.handler;

import com.example.webRTC.dto.ErrorResponse;
import com.example.webRTC.exception.RoomFullException;
import com.example.webRTC.exception.RoomNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RoomNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRoomNotFoundException(RoomNotFoundException e) {
        ErrorResponse response = new ErrorResponse("ROOM_NOT_FOUND", e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RoomFullException.class)
    public ResponseEntity<ErrorResponse> handleRoomFullException(RoomFullException e) {
        ErrorResponse response = new ErrorResponse("ROOM_FULL", e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        ErrorResponse response = new ErrorResponse("INTERNAL_SERVER_ERROR", e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
