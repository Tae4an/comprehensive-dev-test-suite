package com.example.webRTC.controller;

import com.example.webRTC.dto.CreateRoomRequest;
import com.example.webRTC.dto.JoinRoomRequest;
import com.example.webRTC.dto.JoinRoomResponse;
import com.example.webRTC.dto.RoomResponse;
import com.example.webRTC.service.WebRTCService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "WebRTC", description = "WebRTC 화상회의 API")
@RestController
@RequestMapping("/api/v1/webrtc")
@RequiredArgsConstructor
public class WebRTCController {

    private final WebRTCService webRTCService;

    @Operation(summary = "방 생성", description = "새로운 화상회의 방을 생성합니다.")
    @PostMapping("/rooms")
    public ResponseEntity<RoomResponse> createRoom(@RequestBody CreateRoomRequest request) {
        try {
            RoomResponse response = webRTCService.createRoom(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @Operation(summary = "방 참여", description = "특정 화상회의 방에 참여합니다.")
    @PostMapping("/rooms/{roomId}/join")
    public ResponseEntity<JoinRoomResponse> joinRoom(
            @Parameter(description = "방 ID") @PathVariable String roomId,
            @RequestBody JoinRoomRequest request) {
        return ResponseEntity.ok(webRTCService.joinRoom(roomId, request));
    }

    @Operation(summary = "방 목록 조회", description = "모든 화상회의 방 목록을 조회합니다.")
    @GetMapping("/rooms")
    public ResponseEntity<List<RoomResponse>> getRooms() {
        return ResponseEntity.ok(webRTCService.getRooms());
    }

    @Operation(summary = "특정 방 조회", description = "특정 화상회의 방의 정보를 조회합니다.")
    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<RoomResponse> getRoom(
            @Parameter(description = "방 ID") @PathVariable String roomId) {
        return ResponseEntity.ok(webRTCService.getRoom(roomId));
    }
}
