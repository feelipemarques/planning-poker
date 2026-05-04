package com.agile.planning_poker.websocket.dto.event;

import com.agile.planning_poker.room.RoomStatus;

public record RoundRestartedEvent(RoomStatus status) {}

