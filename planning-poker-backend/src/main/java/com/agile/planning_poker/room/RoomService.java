package com.agile.planning_poker.room;

import com.agile.planning_poker.participant.Participant;
import com.agile.planning_poker.participant.ParticipantRepository;
import com.agile.planning_poker.websocket.dto.event.NewParticipantEvent;
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

    public void joinRoom(String code, JoinRoomRequest request, String sessionId){
        System.out.println("joinRoom chamado - nickname: " + request.nickname() + " sessionId" + sessionId);
        Room room = roomRepository.findByRoomCode(code)
                .orElseThrow(() -> new RuntimeException("Room not found!"));

        persistParticipant(request.nickname(), room, sessionId);

        String ownerNickname = participantRepository.findByRoomAndIsOwner(room, true)
                .map(Participant::getNickname)
                .orElse("");

        List<String> participants = participantRepository
                .findByRoomAndIsConnected(room, true)
                .stream()
                .map(Participant::getNickname)
                .toList();

        simpMessagingTemplate.convertAndSend("/topic/room/" +
                code +
                "/participants", new NewParticipantEvent(participants, ownerNickname));

    }

    private Participant persistParticipant(String nickname, Room room, String sessionId){
        return participantRepository.findByNicknameAndRoom(nickname, room)
                .map(existing -> {
                    existing.setSessionId(sessionId);
                    existing.setIsConnected(true);
                    return participantRepository.save(existing);
                })
                .orElseGet(() -> {
                    Participant participant = new Participant();
                    participant.setNickname(nickname);
                    participant.setRoom(room);
                    participant.setSessionId(sessionId);
                    participant.setIsOwner(participantRepository.findByRoom(room).isEmpty());
                    participant.setIsConnected(true);
                    return participantRepository.save(participant);
                });
    }

}
