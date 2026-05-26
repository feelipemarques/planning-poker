package com.agile.planning_poker.exception;

public class UserStoryNotFoundException extends RuntimeException {
    public UserStoryNotFoundException(Long userStoryId) {
        super("UserStoryID " + userStoryId + " not found!");
    }
}
