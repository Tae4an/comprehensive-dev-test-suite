package com.example.webRTC.repository;

import com.example.webRTC.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    // 특정 방의 참가자 목록 조회
    @Query("SELECT p FROM Participant p WHERE p.room.roomId = :roomId")
    List<Participant> findByRoomId(@Param("roomId") String roomId);
    
    // 특정 사용자가 참여한 방 찾기
    Optional<Participant> findByUserIdAndRoom_RoomId(String userId, String roomId);
    
    // 특정 사용자의 모든 참여 정보 조회
    List<Participant> findByUserId(String userId);
    
    // 특정 방에서 사용자 삭제
    void deleteByUserIdAndRoom_RoomId(String userId, String roomId);
}