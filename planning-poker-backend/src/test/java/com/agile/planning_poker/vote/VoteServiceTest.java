package com.agile.planning_poker.vote;

import com.agile.planning_poker.participant.ParticipantRepository;
import com.agile.planning_poker.userstory.UserStoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

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

    @Test
    void shouldCalculateEstimate() {
        Vote vote1 = new Vote();
        vote1.setValue("2");

        Vote vote2 = new Vote();
        vote2.setValue("10");
    }


}
