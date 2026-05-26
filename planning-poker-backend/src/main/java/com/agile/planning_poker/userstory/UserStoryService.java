package com.agile.planning_poker.userstory;

import com.agile.planning_poker.exception.RoomNotFoundException;
import com.agile.planning_poker.room.RoomRepository;
import com.agile.planning_poker.websocket.dto.event.StoryEvent;
import com.agile.planning_poker.websocket.dto.request.CreateStoryRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserStoryService {

    private final UserStoryRepository userStoryRepository;
    private final RoomRepository roomRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public void createStory(String code, CreateStoryRequest request){
        UserStory userStory = new UserStory();
        userStory.setRoom(roomRepository.findByRoomCode(code).orElseThrow(() -> new RoomNotFoundException(code)));
        userStory.setName(request.storyName());
        userStory.setDescription(request.storyDescription());
        userStory.setStoryPriority(request.storyPriority());

        userStoryRepository.save(userStory);

        simpMessagingTemplate.convertAndSend("/topic/room/" + code + "/stories", new StoryEvent(userStory.getId(), userStory.getName(), userStory.getDescription(), userStory.getStoryPriority()));
    }

}
