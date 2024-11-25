package com.example.webRTC.repository;

import com.example.webRTC.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findByRoomId(String roomId);
    
    // 활성화된 방 목록 조회 (참가자가 있는 방)
    @Query("SELECT DISTINCT r FROM Room r LEFT JOIN FETCH r.participants WHERE SIZE(r.participants) > 0")
    List<Room> findActiveRooms();
    
    // 방 이름으로 조회
    Optional<Room> findByRoomName(String roomName);
    
    // 참가자 수가 제한 인원보다 적은 방들 조회
    @Query("SELECT r FROM Room r WHERE SIZE(r.participants) < r.maxParticipants")
    List<Room> findAvailableRooms();
}