package com.agile.planning_poker.vote;

import com.agile.planning_poker.participant.Participant;
import com.agile.planning_poker.participant.ParticipantRepository;
import com.agile.planning_poker.room.RoomStatus;
import com.agile.planning_poker.userstory.UserStory;
import com.agile.planning_poker.userstory.UserStoryRepository;
import com.agile.planning_poker.websocket.dto.event.RoundRestartedEvent;
import com.agile.planning_poker.websocket.dto.event.VoteEvent;
import com.agile.planning_poker.websocket.dto.event.VotesRevealedEvent;
import com.agile.planning_poker.websocket.dto.event.VotingStartedEvent;
import com.agile.planning_poker.websocket.dto.request.CastVoteRequest;
import com.agile.planning_poker.websocket.dto.request.RestartRoundRequest;
import com.agile.planning_poker.websocket.dto.request.RevealCardsRequest;
import com.agile.planning_poker.websocket.dto.request.StartRoundRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class VoteService {

    private final ParticipantRepository participantRepository;
    private final UserStoryRepository userStoryRepository;
    private final VoteRepository voteRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public void castVote(String code, CastVoteRequest request, String sessionId){
        Participant participant = participantRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new RuntimeException("Participant not found!"));

        UserStory userStory = userStoryRepository.findById(request.userStoryId())
                .orElseThrow(() -> new RuntimeException("UserStory not found!"));

        if(voteRepository.findByUserStoryAndParticipant(userStory, participant).isPresent()){
            throw new RuntimeException("Participant already voted!");
        }

        Vote vote = new Vote();
        vote.setParticipant(participant);
        vote.setUserStory(userStory);
        vote.setValue(request.value());

        voteRepository.save(vote);

        simpMessagingTemplate.convertAndSend("/topic/room/" + code + "/votes", new VoteEvent(participant.getNickname(), Boolean.TRUE, "VOTE_CAST"));
    }

    public void revealCards(String code, RevealCardsRequest request, String sessionId){

        Participant participant = participantRepository.findBySessionId(sessionId).orElseThrow(() -> new RuntimeException("Participant not found!"));

        if(participant.getIsOwner()){
            var userStory = userStoryRepository.findById(request.userStoryId()).orElseThrow(() -> new RuntimeException("UserStory not found!"));
            List<Vote> currentUserStoryVotes = voteRepository.findByUserStory(userStory);

            String finalEstimate = calculateEstimate(currentUserStoryVotes);
            userStory.setFinalEstimate(finalEstimate);
            userStoryRepository.save(userStory);
            simpMessagingTemplate.convertAndSend("/topic/room/" + code + "/votes", new VotesRevealedEvent(currentUserStoryVotes.stream().map(Vote::getValue).toList(), finalEstimate, "VOTES_REVEALED"));
        }else{
            throw new RuntimeException("Not allowed!");
        }
    }

    private String calculateEstimate(List<Vote> votes){
        double sum = 0.0;
        for(Vote vote : votes){
            sum += Double.parseDouble(vote.getValue());
        }
        Double average = sum / votes.size();
        return String.valueOf(average);
    }

    public void startVoting(String code, StartRoundRequest request, String sessionId){
        Participant participant = participantRepository.findBySessionId(sessionId).orElseThrow(() -> new RuntimeException("Participant not found!"));

        if(!participant.getIsOwner()){
            throw new RuntimeException("Not Allowed!");
        }else{
            UserStory userStory = userStoryRepository.findById(request.storyId()).orElseThrow(() -> new RuntimeException("UserStory not found!"));
            simpMessagingTemplate.convertAndSend("/topic/room/" + code + "/round", new VotingStartedEvent(userStory.getId(), userStory.getName(), RoomStatus.VOTING, "VOTING_STARTED"));
        }
    }

    public void restartRound(String code, RestartRoundRequest request, String sessionId){
        Participant participant = participantRepository.findBySessionId(sessionId).orElseThrow(() -> new RuntimeException("Participant not found!"));

        if(!participant.getIsOwner()){
            throw new RuntimeException("Not Allowed!");
        }else{
            UserStory userStory = userStoryRepository.findById(request.userStoryId()).orElseThrow(() -> new RuntimeException("UserStory not found!"));
            voteRepository.deleteByUserStory(userStory);
            userStory.setFinalEstimate("");
            userStoryRepository.save(userStory);
            simpMessagingTemplate.convertAndSend("/topic/room/" + code + "/round", new RoundRestartedEvent(RoomStatus.WAITING, "ROUND_RESTARTED"));
        }
    }


}
