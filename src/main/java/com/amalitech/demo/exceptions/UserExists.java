package com.amalitech.demo.exceptions;

public class UserExists extends RuntimeException{
    public UserExists(String message) {
        super(message);
    }
}
