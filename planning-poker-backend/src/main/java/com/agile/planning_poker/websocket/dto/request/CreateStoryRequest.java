package com.agile.planning_poker.websocket.dto.request;

import com.agile.planning_poker.userstory.Priority;

public record CreateStoryRequest(String name, String description, Priority priority) {}

