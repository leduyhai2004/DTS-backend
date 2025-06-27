package com.example.usermanagement.exception;

public class UserExistedException extends RuntimeException {
    public UserExistedException(String message) {
        super("User already exists: " + message);
    }
}

