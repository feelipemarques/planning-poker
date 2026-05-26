package com.agile.planning_poker.vote;

import com.agile.planning_poker.exception.AlreadyVotedException;
import com.agile.planning_poker.exception.NotAllowedException;
import com.agile.planning_poker.exception.ParticipantNotFoundException;
import com.agile.planning_poker.exception.UserStoryNotFoundException;
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

        Participant participant = findParticipant(sessionId);
        UserStory userStory = findUserStory(request.userStoryId());

        if(voteRepository.findByUserStoryAndParticipant(userStory, participant).isPresent()){
            throw new AlreadyVotedException("Participant already voted!");
        }

        Vote vote = new Vote();
        vote.setParticipant(participant);
        vote.setUserStory(userStory);
        vote.setValue(request.value());

        voteRepository.save(vote);

        simpMessagingTemplate.convertAndSend("/topic/room/" + code + "/votes", new VoteEvent(participant.getNickname(), Boolean.TRUE, "VOTE_CAST"));
    }

    public void revealCards(String code, RevealCardsRequest request, String sessionId){

        Participant participant = findParticipant(sessionId);

        if(participant.getIsOwner()){
            UserStory userStory = findUserStory(request.userStoryId());

            List<Vote> currentUserStoryVotes = voteRepository.findByUserStory(userStory);

            String finalEstimate = calculateEstimate(currentUserStoryVotes);
            userStory.setFinalEstimate(finalEstimate);

            userStoryRepository.save(userStory);
            simpMessagingTemplate.convertAndSend("/topic/room/" + code + "/votes", new VotesRevealedEvent(currentUserStoryVotes.stream().map(Vote::getValue).toList(), finalEstimate, "VOTES_REVEALED"));
        }else{
            throw new NotAllowedException();
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

        Participant participant = findParticipant(sessionId);

        if(!participant.getIsOwner()){
            throw new NotAllowedException();
        }else{
            UserStory userStory = findUserStory(request.storyId());
            simpMessagingTemplate.convertAndSend("/topic/room/" + code + "/round", new VotingStartedEvent(userStory.getId(), userStory.getName(), RoomStatus.VOTING, "VOTING_STARTED"));
        }
    }

    public void restartRound(String code, RestartRoundRequest request, String sessionId){

        Participant participant = findParticipant(sessionId);

        if(!participant.getIsOwner()){
            throw new NotAllowedException();
        }else{
            UserStory userStory = findUserStory(request.userStoryId());

            voteRepository.deleteByUserStory(userStory);
            userStory.setFinalEstimate("");
            userStoryRepository.save(userStory);
            simpMessagingTemplate.convertAndSend("/topic/room/" + code + "/round", new RoundRestartedEvent(RoomStatus.WAITING, "ROUND_RESTARTED"));
        }
    }

    private Participant findParticipant(String sessionId){
        return participantRepository
                .findBySessionId(sessionId)
                .orElseThrow(() -> new ParticipantNotFoundException(sessionId));
    }

    private UserStory findUserStory(Long id){
        return userStoryRepository
                .findById(id)
                .orElseThrow(() -> new UserStoryNotFoundException(id));
    }


}
