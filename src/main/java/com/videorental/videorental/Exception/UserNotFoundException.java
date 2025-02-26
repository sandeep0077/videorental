package com.videorental.videorental.Exception;

public class UserNotFoundException extends RuntimeException{

    // constructor
    public UserNotFoundException(String message){
        super(message);
    }
}
