package com.agile.planning_poker.websocket.dto.event;

import java.util.List;

public record VotesRevealedEvent(List<String> votes, String finalEstimate) {
}
