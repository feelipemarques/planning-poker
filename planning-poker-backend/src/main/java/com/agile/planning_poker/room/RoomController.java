package com.agile.planning_poker.room;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping("/room")
    public ResponseEntity<String> createRoom(){
        String roomCode = roomService.createRoom();
        return ResponseEntity.ok(roomCode);
    }
}
