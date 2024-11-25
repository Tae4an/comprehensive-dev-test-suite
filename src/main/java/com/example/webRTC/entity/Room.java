package com.example.webRTC.entity;

import com.example.webRTC.exception.RoomFullException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "rooms")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String roomId;

    @Column(nullable = false)
    private String roomName;

    @Column(nullable = false)
    private Integer maxParticipants;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Participant> participants = new ArrayList<>();

    @CreatedDate
    private LocalDateTime createdAt;

    @Builder
    public Room(String roomName, Integer maxParticipants) {
        this.roomId = UUID.randomUUID().toString();
        this.roomName = roomName;
        this.maxParticipants = maxParticipants;
    }

    public void addParticipant(Participant participant) {
        if (participants.size() >= maxParticipants) {
            throw new RoomFullException("Room is full");
        }
        participants.add(participant);
        participant.setRoom(this);
    }
}
