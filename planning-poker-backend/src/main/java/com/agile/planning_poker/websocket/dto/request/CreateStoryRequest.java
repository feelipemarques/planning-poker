package com.agile.planning_poker.websocket.dto.request;

import com.agile.planning_poker.userstory.Priority;

public record CreateStoryRequest(String storyName, String storyDescription, Priority storyPriority) {}

