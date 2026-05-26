package com.agile.planning_poker.exception;

public class RoomNotFoundException extends RuntimeException{
    public RoomNotFoundException(String code) {
        super("Room not found: " + code);
    }
}
