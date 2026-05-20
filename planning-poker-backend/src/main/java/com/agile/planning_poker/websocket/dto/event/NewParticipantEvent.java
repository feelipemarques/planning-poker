package com.agile.planning_poker.websocket.dto.event;

import java.util.List;

public record NewParticipantEvent(List<String> participants, String ownerNickname){
}
