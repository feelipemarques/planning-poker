package com.agile.planning_poker.room;

import com.agile.planning_poker.participant.Participant;
import com.agile.planning_poker.participant.ParticipantRepository;
import com.agile.planning_poker.userstory.UserStory;
import com.agile.planning_poker.userstory.UserStoryRepository;
import com.agile.planning_poker.vote.VoteRepository;
import com.agile.planning_poker.websocket.dto.event.NewParticipantEvent;
import com.agile.planning_poker.websocket.dto.event.StoryEvent;
import com.agile.planning_poker.websocket.dto.request.CreateStoryRequest;
import com.agile.planning_poker.websocket.dto.request.JoinRoomRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final ParticipantRepository participantRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public String createRoom(){
        Room randomRoom = new Room();
        randomRoom.setRoomCode(UUID.randomUUID().toString().substring(0, 5).toUpperCase());
        randomRoom.setStatus(RoomStatus.WAITING);
        roomRepository.save(randomRoom);
        return randomRoom.getRoomCode();
    }

    public void joinRoom(String code, JoinRoomRequest request){
        Room room = roomRepository.findByRoomCode(code)
                .orElseThrow(() -> new RuntimeException("Room not found!"));

        persistParticipant(request.nickname(), room);

        List<String> participants = participantRepository
                .findByRoom(room)
                .stream()
                .map(Participant::getNickname)
                .toList();

        simpMessagingTemplate.convertAndSend("/topic/room/" +
                code +
                "/participants", new NewParticipantEvent(participants));

    }

    private void persistParticipant(String nickname, Room room){
        Participant participant = new Participant();

        participant.setIsOwner(participantRepository.findByRoom(room).isEmpty());
        participant.setNickname(nickname);
        participant.setRoom(room);

        participantRepository.save(participant);
    }





}
