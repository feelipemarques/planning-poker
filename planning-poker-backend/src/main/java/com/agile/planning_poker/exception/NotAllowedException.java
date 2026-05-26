package com.agile.planning_poker.exception;

public class NotAllowedException extends RuntimeException{
    public NotAllowedException(){
        super("Only the owner is allowed to take this action!");
    }
}
