package com.agile.planning_poker.vote;

import com.agile.planning_poker.participant.Participant;
import com.agile.planning_poker.participant.ParticipantRepository;
import com.agile.planning_poker.room.Room;
import com.agile.planning_poker.userstory.UserStory;
import com.agile.planning_poker.userstory.UserStoryRepository;
import com.agile.planning_poker.websocket.dto.request.CastVoteRequest;
import com.agile.planning_poker.websocket.dto.request.RevealCardsRequest;
import com.agile.planning_poker.websocket.dto.request.StartRoundRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VoteServiceTest {

    @Mock
    private ParticipantRepository participantRepository;
    @Mock
    private UserStoryRepository userStoryRepository;
    @Mock
    private VoteRepository voteRepository;
    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @InjectMocks
    private VoteService voteService;

    private Participant participant;
    private UserStory userStory;
    private String session;
    private Room room;

    @BeforeEach
    void setup(){
        room = new Room();
        room.setRoomCode("TES12");

        session = "SESSION";
        participant = new Participant();
        participant.setSessionId(session);

        userStory = new UserStory();

        when(participantRepository.findBySessionId(session)).thenReturn(Optional.of(participant));
        lenient().when(userStoryRepository.findById(10L)).thenReturn(Optional.of(userStory));

    }

    @Test
    void shouldCalculateEstimate() {
        RevealCardsRequest request = new RevealCardsRequest(10L);

        participant.setIsOwner(true);

        Vote vote1 = new Vote();
        vote1.setValue("10");

        Vote vote2 = new Vote();
        vote2.setValue("4");

        when(voteRepository.findByUserStory(userStory)).thenReturn(List.of(vote1, vote2));

        voteService.revealCards("TES12", request, participant.getSessionId());

        assertEquals("7.0", userStory.getFinalEstimate());
    }

    @Test
    void shouldThrowAnExceptionWhenSomeoneTryToVoteTwice() {
        lenient().when(voteRepository.findByUserStoryAndParticipant(userStory, participant)).thenReturn(Optional.of(participant));
        assertThrows(RuntimeException.class, () -> voteService.castVote("TES12", new CastVoteRequest("10", userStory.getId(), "TES12"), session));
    }

    @Test
    void shouldThrowAnExceptionWhenParticipantDoesNotExist() {
        when(participantRepository.findBySessionId(session)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> voteService.castVote("TES12", new CastVoteRequest("10", userStory.getId(), "TES12"), session));
    }

    @Test
    void shouldThrowAnExpcetionWhenRevealCardsIsTriggeredFromNotOwner(){
        assertThrows(RuntimeException.class, () -> voteService.startVoting("TES12", new StartRoundRequest(10L), session));
    }

    @Test
    void shouldThrowAnExceptionWhenStartVotingIsTriggeredFromNotOwner(){
        assertThrows(RuntimeException.class, () -> voteService.revealCards("TES12", new RevealCardsRequest(10L), session));
    }

}
