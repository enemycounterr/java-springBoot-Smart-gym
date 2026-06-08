package com.sprint.training.exceptions;

public class ZoneAccessDeniedException extends RuntimeException{
    public ZoneAccessDeniedException(String message) {
        super(message);
    }
}
