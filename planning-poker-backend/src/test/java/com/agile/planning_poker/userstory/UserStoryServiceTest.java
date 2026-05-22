package com.agile.planning_poker.userstory;

import com.agile.planning_poker.room.Room;
import com.agile.planning_poker.room.RoomRepository;
import com.agile.planning_poker.websocket.dto.request.CreateStoryRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserStoryServiceTest {

    @Mock
    private UserStoryRepository userStoryRepository;
    @Mock
    private RoomRepository roomRepository;
    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @InjectMocks
    private UserStoryService userStoryService;

    @Test
    void shouldThrowAnExceptionWhenRoomDoesNotExist(){
        UserStory userStory = new UserStory();
        CreateStoryRequest storyRequest = new CreateStoryRequest("Name", "Desc", Priority.MUST_HAVE);
        assertThrows(RuntimeException.class, () -> userStoryService.createStory("TES12", storyRequest));
    }

    @Test
    void shouldSaveUserStoryCorrectly(){
        Room room = new Room();
        room.setRoomCode("TES12");

        CreateStoryRequest request =
                new CreateStoryRequest(
                        "Test",
                        "Creating an user story",
                        Priority.MUST_HAVE
                );

        when(roomRepository.findByRoomCode("TES12")).thenReturn(Optional.of(room));

        userStoryService.createStory("TES12", request);

        ArgumentCaptor<UserStory> captor = ArgumentCaptor.forClass(UserStory.class);
        verify(userStoryRepository).save(captor.capture());
        UserStory savedStory = captor.getValue();

        assertEquals("Test", savedStory.getName());
        assertEquals("Creating an user story", savedStory.getDescription());
        assertEquals(Priority.MUST_HAVE, savedStory.getStoryPriority());
        assertEquals(room, savedStory.getRoom());
    }



}
