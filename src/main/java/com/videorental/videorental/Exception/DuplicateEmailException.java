package com.videorental.videorental.Exception;

public class DuplicateEmailException extends  RuntimeException{

    public DuplicateEmailException(String message){
        super(message);
    }
}
