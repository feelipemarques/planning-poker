package com.agile.planning_poker.websocket.controller;

import com.agile.planning_poker.room.RoomService;
import com.agile.planning_poker.userstory.UserStory;
import com.agile.planning_poker.userstory.UserStoryService;
import com.agile.planning_poker.vote.VoteService;
import com.agile.planning_poker.websocket.dto.request.*;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final RoomService roomService;
    private final VoteService voteService;
    private final UserStoryService userStoryService;

    @MessageMapping("/room/{code}/join")
    public void joinRoom(@DestinationVariable String code, JoinRoomRequest request, SimpMessageHeaderAccessor headerAccessor){
        String sessionId = headerAccessor.getSessionId();
        roomService.joinRoom(code, request, sessionId);
    }

    @MessageMapping("/room/{code}/create")
    public void createStory(@DestinationVariable String code, CreateStoryRequest request){
        userStoryService.createStory(code, request);
    }

    @MessageMapping("/room/{code}/vote")
    public void castVote(@DestinationVariable String code, CastVoteRequest request, SimpMessageHeaderAccessor headerAccessor){
        String sessionId = headerAccessor.getSessionId();
        voteService.castVote(code, request, sessionId);
    }

    @MessageMapping("/room/{code}/reveal")
    public void revealCards(@DestinationVariable String code, RevealCardsRequest request, SimpMessageHeaderAccessor headerAccessor){
        String sessionId = headerAccessor.getSessionId();
        voteService.revealCards(code, request, sessionId);
    }

    @MessageMapping("/room/{code}/restart")
    public void restartRound(@DestinationVariable String code, RestartRoundRequest request, SimpMessageHeaderAccessor headerAccessor){
        String sessionId = headerAccessor.getSessionId();
        voteService.restartRound(code, request, sessionId);
    }



}
