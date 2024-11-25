package com.example.websocket.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.webRTC.service.WebRTCService;
import com.example.websocket.dto.WebSocketMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class SignalingHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final WebRTCService webRTCService;

    // 세션 저장소
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    // 방 참여자 저장소: roomId -> (userId -> session)
    private final Map<String, Map<String, WebSocketSession>> rooms = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String sessionId = session.getId();
        log.info("WebSocket 연결 성공: {}", sessionId);
        sessions.put(sessionId, session);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.debug("받은 메시지: {}", payload);
        
        try {
            WebSocketMessage webSocketMessage = objectMapper.readValue(payload, WebSocketMessage.class);
            log.debug("파싱된 메시지: {}", webSocketMessage);

            switch (webSocketMessage.getType()) {
                case "join":
                    handleJoinMessage(session, webSocketMessage);
                    break;
                case "offer":
                case "answer":
                case "ice":
                    handleSignalingMessage(session, webSocketMessage);
                    break;
                case "leave":
                    handleLeaveMessage(session, webSocketMessage);
                    break;
                default:
                    log.warn("알 수 없는 메시지 타입: {}", webSocketMessage.getType());
            }
        } catch (Exception e) {
            log.error("메시지 처리 중 오류", e);
            handleError(session, e.getMessage());
        }
    }

    private void handleJoinMessage(WebSocketSession session, WebSocketMessage message) {
        String roomId = message.getRoomId();
        String userId = message.getUserId();

        try {
            // 방 존재 여부 확인 (webRTCService 활용)
            webRTCService.getRoom(roomId);

            // 방 참여자 관리
            rooms.computeIfAbsent(roomId, k -> new ConcurrentHashMap<>())
                 .put(userId, session);

            // 다른 참여자들에게 새 참여자 알림
            notifyOthersInRoom(roomId, userId, new WebSocketMessage(
                "user-joined",
                roomId,
                userId,
                null
            ));

            log.info("사용자 {} 가 방 {} 에 참여함", userId, roomId);
        } catch (Exception e) {
            log.error("방 참여 중 오류", e);
            handleError(session, "방 참여 실패: " + e.getMessage());
        }
    }

    private void handleSignalingMessage(WebSocketSession session, WebSocketMessage message) {
        String roomId = message.getRoomId();
        Map<String, WebSocketSession> roomParticipants = rooms.get(roomId);
        
        if (roomParticipants != null) {
            roomParticipants.forEach((userId, participantSession) -> {
                if (!participantSession.getId().equals(session.getId())) {
                    sendMessage(participantSession, message);
                }
            });
        }
    }

    private void handleLeaveMessage(WebSocketSession session, WebSocketMessage message) {
        String roomId = message.getRoomId();
        String userId = message.getUserId();
        
        Map<String, WebSocketSession> roomParticipants = rooms.get(roomId);
        if (roomParticipants != null) {
            roomParticipants.remove(userId);
            
            if (roomParticipants.isEmpty()) {
                rooms.remove(roomId);
            } else {
                notifyOthersInRoom(roomId, userId, new WebSocketMessage(
                    "user-left",
                    roomId,
                    userId,
                    null
                ));
            }
        }
        
        log.info("사용자 {} 가 방 {} 에서 퇴장함", userId, roomId);
    }

    private void notifyOthersInRoom(String roomId, String userId, WebSocketMessage message) {
        Map<String, WebSocketSession> roomParticipants = rooms.get(roomId);
        if (roomParticipants != null) {
            roomParticipants.forEach((participantId, session) -> {
                if (!participantId.equals(userId)) {
                    sendMessage(session, message);
                }
            });
        }
    }

    private void handleError(WebSocketSession session, String errorMessage) {
        WebSocketMessage errorResponse = new WebSocketMessage(
            "error",
            null,
            null,
            errorMessage
        );
        sendMessage(session, errorResponse);
    }

    private void sendMessage(WebSocketSession session, WebSocketMessage message) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(message);
            session.sendMessage(new TextMessage(jsonMessage));
        } catch (Exception e) {
            log.error("메시지 전송 실패", e);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String sessionId = session.getId();
        log.info("WebSocket 연결 종료: {}", sessionId);
        
        // 세션 제거
        sessions.remove(sessionId);
        
        // 모든 방에서 해당 세션을 가진 참여자 제거
        rooms.forEach((roomId, participants) -> {
            String userId = participants.entrySet().stream()
                .filter(entry -> entry.getValue().getId().equals(sessionId))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);

            if (userId != null) {
                handleLeaveMessage(session, new WebSocketMessage(
                    "leave",
                    roomId,
                    userId,
                    null
                ));
            }
        });
    }
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("WebSocket 전송 에러: {}", exception.getMessage());
    }
}