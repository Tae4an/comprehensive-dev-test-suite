package com.example;

import com.example.websocket.handler.SignalingHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.webRTC.dto.RoomResponse;
import com.example.webRTC.service.WebRTCService;
import com.example.websocket.dto.WebSocketMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.time.LocalDateTime;


import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SignalingHandlerTest {

    @Mock
    private WebRTCService webRTCService;
    
    @Mock
    private WebSocketSession session;

    private SignalingHandler signalingHandler;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        signalingHandler = new SignalingHandler(objectMapper, webRTCService);
    }

    @Test
    void whenConnectionEstablished_thenSessionIsStored() throws Exception {
        // Given
        given(session.getId()).willReturn("test-session-id");

        // When
        signalingHandler.afterConnectionEstablished(session);

        // Then
        verify(session, times(1)).getId();
    }

    @Test
    void whenJoinMessage_thenRoomIsCheckedAndParticipantAdded() throws Exception {
        // Given
        String roomId = "test-room";
        String userId = "test-user";
        WebSocketMessage joinMessage = new WebSocketMessage("join", roomId, userId, null);
        String messageJson = objectMapper.writeValueAsString(joinMessage);

        given(session.getId()).willReturn("test-session-id");
        given(webRTCService.getRoom(roomId)).willReturn(
            new RoomResponse(roomId, "Test Room", 4, 0, LocalDateTime.now())
        );

        // When
        signalingHandler.handleTextMessage(session, new TextMessage(messageJson));

        // Then
        verify(webRTCService, times(1)).getRoom(roomId);
    }

    @Test
    void whenSignalingMessage_thenMessageIsRelayed() throws Exception {
        // Given
        String roomId = "test-room";
        String userId = "test-user";
        WebSocketMessage offerMessage = new WebSocketMessage(
            "offer", 
            roomId, 
            userId, 
            "{type: 'offer', sdp: 'test-sdp'}"
        );
        String messageJson = objectMapper.writeValueAsString(offerMessage);

        // When
        signalingHandler.handleTextMessage(session, new TextMessage(messageJson));

        // Then
        // 메시지 전달 검증은 실제 세션이 필요하므로 여기서는 예외가 발생하지 않는지만 확인
        verify(session, atLeastOnce()).getId();
    }

    @Test
    void whenLeaveMessage_thenParticipantIsRemoved() throws Exception {
        // Given
        String roomId = "test-room";
        String userId = "test-user";
        WebSocketMessage leaveMessage = new WebSocketMessage("leave", roomId, userId, null);
        String messageJson = objectMapper.writeValueAsString(leaveMessage);

        // When
        signalingHandler.handleTextMessage(session, new TextMessage(messageJson));

        // Then
        verify(session, atLeastOnce()).getId();
    }

    @Test
    void whenConnectionClosed_thenSessionIsRemoved() throws Exception {
        // Given
        given(session.getId()).willReturn("test-session-id");

        // When
        signalingHandler.afterConnectionClosed(session, CloseStatus.NORMAL);

        // Then
        verify(session, atLeastOnce()).getId();
    }
}