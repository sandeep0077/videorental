package com.videorental.videorental.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto {

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;     // Email for user identification

    @NotBlank(message = "Password is required")
    private String password;  // Password for authentication

}
