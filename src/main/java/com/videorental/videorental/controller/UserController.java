package com.videorental.videorental.controller;


import com.videorental.videorental.auth.JwtTokenProvider;
import com.videorental.videorental.dto.LoginDto;
import com.videorental.videorental.dto.UserDto;
import com.videorental.videorental.service.CustomUserDetailsService;
import com.videorental.videorental.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
public class UserController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtTokenProvider tokenProvider;

    // login , registration

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@RequestBody UserDto userDto){
        UserDto savedUser = userService.registerUser(userDto);
        return new ResponseEntity<>(savedUser , HttpStatus.CREATED);
    }

/*
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody LoginDto loginDto){
        // Here, loginDTO will contain the email and password from the request body
        // Spring Security handles the authentication process automatically
        // If authentication is successful, return a success message (or token, if needed)

        return new ResponseEntity<>("Login Successful", HttpStatus.OK);
    Conclusion:
            🔹 If you’re using Basic Authentication, Spring Security already takes care of login.
            🔹 If you need JWT or custom logic, you have to write a login API.
    }
*/

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody LoginDto loginDto) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getEmail() ,
                            loginDto.getPassword()));

            // Generate token directly using email from login DTO
            String jwt = tokenProvider.generateToken(loginDto.getEmail());

            return new ResponseEntity<>(jwt, HttpStatus.OK);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>("Incorrect UserName or Password",HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long userId, @RequestBody @Valid UserDto userDto) {
        UserDto updatedUser = userService.updateUser(userId, userDto);
        return ResponseEntity.ok(updatedUser);
    }
}
