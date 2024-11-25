package com.example.webRTC.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JoinRoomResponse {
    private String roomId;
    private String userId;
    private List<ParticipantResponse> participants;
}
