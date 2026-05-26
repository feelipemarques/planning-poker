package com.agile.planning_poker.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RoomNotFoundException.class)
    public ResponseEntity<String> handleRoomNotFound(RoomNotFoundException ex){
        return ResponseEntity.status(404).body(ex.getMessage());
    }

    @ExceptionHandler(UserStoryNotFoundException.class)
    public ResponseEntity<String> handleUserStoryNotFound(UserStoryNotFoundException ex){
        return ResponseEntity.status(404).body(ex.getMessage());
    }

    @ExceptionHandler(ParticipantNotFoundException.class)
    public ResponseEntity<String> handleParticipantNotFound(ParticipantNotFoundException ex){
        return ResponseEntity.status(404).body(ex.getMessage());
    }

    @ExceptionHandler(NotAllowedException.class)
    public ResponseEntity<String> handleNotAllowedRequests(NotAllowedException ex){
        return ResponseEntity.status(403).body(ex.getMessage());
    }

    @ExceptionHandler(AlreadyVotedException.class)
    public ResponseEntity<String> handleAlreadyVotedException(AlreadyVotedException ex){
        return ResponseEntity.status(409).body(ex.getMessage());
    }

}
