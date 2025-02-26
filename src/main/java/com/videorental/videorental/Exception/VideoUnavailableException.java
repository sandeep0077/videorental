package com.videorental.videorental.Exception;

public class VideoUnavailableException extends RuntimeException {
    public VideoUnavailableException(String message) {
        super(message);
    }
}
