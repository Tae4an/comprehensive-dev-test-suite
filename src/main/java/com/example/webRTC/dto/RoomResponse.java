package com.example.webRTC.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponse {
    private String roomId;
    private String roomName;
    private Integer maxParticipants;
    private Integer currentParticipants;
    private LocalDateTime createdAt;
}
