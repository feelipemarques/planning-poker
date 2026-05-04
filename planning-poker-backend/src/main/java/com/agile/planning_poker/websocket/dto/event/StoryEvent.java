package com.agile.planning_poker.websocket.dto.event;

import com.agile.planning_poker.userstory.Priority;

public record StoryEvent (String storyName, String storyDescription, Priority priority){
}
