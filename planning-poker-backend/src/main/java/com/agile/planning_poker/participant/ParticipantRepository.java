package com.agile.planning_poker.participant;

import com.agile.planning_poker.room.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    public List<Participant> findByRoom(Room room);
    public Optional<Participant> findBySessionId(String sessionId);
}
