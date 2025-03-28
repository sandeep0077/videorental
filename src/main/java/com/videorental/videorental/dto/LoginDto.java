package com.videorental.videorental.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
public class LoginDto {

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;     // Email for user identification

    @NotBlank(message = "Password is required")
    private String password;  // Password for authentication

    public LoginDto() {
    }

    public LoginDto(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

}
