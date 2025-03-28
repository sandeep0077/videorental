package com.videorental.videorental;

import io.jsonwebtoken.security.Keys;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.security.Key;
import java.util.Base64;

@SpringBootApplication
public class VideorentalApplication {

	public static void main(String[] args) {

		SpringApplication.run(VideorentalApplication.class, args);
	}

}
