package com.example.webRTC.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantResponse {
    private String userId;
    private String username;
    private LocalDateTime joinedAt;
}