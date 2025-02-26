package com.videorental.videorental.Exception;

public class UserPendingLateFeesException extends RuntimeException {
    public UserPendingLateFeesException(String message) {
        super(message);
    }
}
