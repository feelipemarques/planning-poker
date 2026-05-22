package com.agile.planning_poker.room;

import com.agile.planning_poker.participant.Participant;
import com.agile.planning_poker.participant.ParticipantRepository;
import com.agile.planning_poker.websocket.dto.request.JoinRoomRequest;
import jakarta.servlet.http.Part;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @InjectMocks
    private RoomService roomService;

    @Test
    void shouldCreateRoomWithValidCode(){
        String code = roomService.createRoom();
        assertNotNull(code);
        assertEquals(5, code.length());
        assertEquals(code, code.toUpperCase());
    }

    @Test
    void shouldThrowExceptionWhenRoomDoesNotExist(){
        JoinRoomRequest request = new JoinRoomRequest("teste");
        assertThrows(RuntimeException.class, () -> roomService.joinRoom("NOTEX", request, "sessionId"));
    }

    @Test
    void shouldSetOwnerTheFirstParticipant(){

        String roomCode = "TES12";
        String sessionId = "session1";
        JoinRoomRequest request = new JoinRoomRequest("Teste");
        Room room = new Room();

        when(roomRepository.findByRoomCode(roomCode)).thenReturn(Optional.of(room));
        when(participantRepository.findByRoom(any())).thenReturn(List.of());

        roomService.joinRoom(roomCode, request, sessionId);

        ArgumentCaptor<Participant> captor = ArgumentCaptor.forClass(Participant.class);
        verify(participantRepository).save(captor.capture());
        Participant savedParticipant = captor.getValue();

        assertTrue(savedParticipant.getIsOwner());
    }

    @Test
    void shouldNotSetOwnerTheRemainingParticipants(){

        String roomCode = "TES12";
        String sessionId = "session1";
        JoinRoomRequest request = new JoinRoomRequest("Teste");
        Participant participant = new Participant();
        Room room = new Room();

        when(roomRepository.findByRoomCode(roomCode)).thenReturn(Optional.of(room));
        when(participantRepository.findByRoom(any())).thenReturn(List.of(participant));

        roomService.joinRoom(roomCode, request, sessionId);

        ArgumentCaptor<Participant> captor = ArgumentCaptor.forClass(Participant.class);
        verify(participantRepository).save(captor.capture());
        Participant savedParticipant = captor.getValue();

        assertFalse(savedParticipant.getIsOwner());
    }


}
