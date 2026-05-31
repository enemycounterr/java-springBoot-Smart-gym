package com.sprint.training.exceptions;

public class ClientAlreadyExistException extends RuntimeException{
    public ClientAlreadyExistException(String message) {
        super(message);
    }
}
