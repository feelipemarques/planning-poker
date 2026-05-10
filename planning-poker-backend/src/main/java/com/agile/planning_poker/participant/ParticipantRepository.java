package com.agile.planning_poker.participant;

import com.agile.planning_poker.room.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    List<Participant> findByRoom(Room room);
    Optional<Participant> findBySessionId(String sessionId);
    Optional<Participant> findByNicknameAndRoom(String nickname, Room room);
    List<Participant> findByRoomAndIsConnected(Room room, Boolean isConnected);

}
