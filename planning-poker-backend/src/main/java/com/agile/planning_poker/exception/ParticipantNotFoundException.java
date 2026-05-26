package com.agile.planning_poker.exception;

public class ParticipantNotFoundException extends RuntimeException {
    public ParticipantNotFoundException(String sessionId) {
        super("SessionID for participant not found: " + sessionId);
    }
}
