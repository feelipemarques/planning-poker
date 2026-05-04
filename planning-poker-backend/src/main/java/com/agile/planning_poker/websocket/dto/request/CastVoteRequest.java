package com.agile.planning_poker.websocket.dto.request;

public record CastVoteRequest(String value, Long userStoryId, String roomCode) {}

