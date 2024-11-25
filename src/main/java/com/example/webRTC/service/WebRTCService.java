package com.example.webRTC.service;


import com.example.webRTC.dto.*;
import com.example.webRTC.entity.Participant;
import com.example.webRTC.entity.Room;
import com.example.webRTC.exception.RoomNotFoundException;
import com.example.webRTC.repository.ParticipantRepository;
import com.example.webRTC.repository.RoomRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class WebRTCService {

   private final RoomRepository roomRepository;
   private final ParticipantRepository participantRepository;

   @Transactional
   public RoomResponse createRoom(CreateRoomRequest request) {
       // 입력값 검증
       validateCreateRoomRequest(request);

       Room room = Room.builder()
               .roomName(request.getRoomName())
               .maxParticipants(request.getMaxParticipants())
               .build();

       Room savedRoom = roomRepository.save(room);
       return convertToRoomResponse(savedRoom);
   }

   @Transactional
   public JoinRoomResponse joinRoom(String roomId, JoinRoomRequest request) {
       // 입력값 검증
       validateJoinRoomRequest(request);

       Room room = roomRepository.findByRoomId(roomId)
               .orElseThrow(() -> new RoomNotFoundException("방을 찾을 수 없습니다: " + roomId));

       // 중복 참여 검증
       validateDuplicateParticipant(roomId, request.getUserId());

       Participant participant = Participant.builder()
               .userId(request.getUserId())
               .username(request.getUsername())
               .build();

       room.addParticipant(participant);
       participantRepository.save(participant);

       return new JoinRoomResponse(
               room.getRoomId(),
               participant.getUserId(),
               room.getParticipants().stream()
                       .map(this::convertToParticipantResponse)
                       .collect(Collectors.toList())
       );
   }

   public List<RoomResponse> getRooms() {
       return roomRepository.findAll().stream()
               .map(this::convertToRoomResponse)
               .collect(Collectors.toList());
   }

   public RoomResponse getRoom(String roomId) {
       Room room = roomRepository.findByRoomId(roomId)
               .orElseThrow(() -> new RoomNotFoundException("방을 찾을 수 없습니다: " + roomId));
       return convertToRoomResponse(room);
   }

   // 검증 메서드들
   private void validateCreateRoomRequest(CreateRoomRequest request) {
       if (request.getRoomName() == null || request.getRoomName().trim().isEmpty()) {
           throw new IllegalArgumentException("방 이름은 필수입니다.");
       }
       if (request.getMaxParticipants() == null || request.getMaxParticipants() < 2) {
           throw new IllegalArgumentException("최대 참가자 수는 2명 이상이어야 합니다.");
       }
   }

   private void validateJoinRoomRequest(JoinRoomRequest request) {
       if (request.getUserId() == null || request.getUserId().trim().isEmpty()) {
           throw new IllegalArgumentException("사용자 ID는 필수입니다.");
       }
       if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
           throw new IllegalArgumentException("사용자 이름은 필수입니다.");
       }
   }

   private void validateDuplicateParticipant(String roomId, String userId) {
       if (participantRepository.findByUserIdAndRoom_RoomId(userId, roomId).isPresent()) {
           throw new IllegalStateException("이미 방에 참여중인 사용자입니다.");
       }
   }

   // 변환 메서드들
   private RoomResponse convertToRoomResponse(Room room) {
       return new RoomResponse(
               room.getRoomId(),
               room.getRoomName(),
               room.getMaxParticipants(),
               room.getParticipants().size(),
               room.getCreatedAt()
       );
   }

   private ParticipantResponse convertToParticipantResponse(Participant participant) {
       return new ParticipantResponse(
               participant.getUserId(),
               participant.getUsername(),
               participant.getJoinedAt()
       );
   }
}