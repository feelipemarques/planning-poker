package com.agile.planning_poker.websocket.dto.event;

public record VoteEvent(String nickname, Boolean hasVoted){
}
