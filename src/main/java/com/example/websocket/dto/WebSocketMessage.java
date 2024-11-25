package com.example.websocket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketMessage {
    private String type;    // "join", "offer", "answer", "ice", "leave"
    private String roomId;
    private String userId;
    private Object data;    // SDP 또는 ICE candidate 정보
}