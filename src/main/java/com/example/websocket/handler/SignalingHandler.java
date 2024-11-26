package com.example.websocket.handler;


import com.example.websocket.dto.WebSocketMessage;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;


@Controller
public class SignalingHandler {

    @MessageMapping("/join/{roomId}")
    @SendTo("/topic/room/{roomId}")
    public WebSocketMessage handleJoin(@DestinationVariable String roomId, WebSocketMessage message) {
        return message;
    }

    @MessageMapping("/signal/{roomId}")
    @SendTo("/topic/room/{roomId}")
    public WebSocketMessage handleSignal(@DestinationVariable String roomId, WebSocketMessage message) {
        return message;
    }
}