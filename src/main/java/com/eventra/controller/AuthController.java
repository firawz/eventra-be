package com.eventra.controller;

import com.eventra.config.JwtUtil;
import com.eventra.dto.ApiResponse;
import com.eventra.dto.AuthRequest;
import com.eventra.dto.RegisterRequest;
import com.eventra.dto.UserResponse;
import com.eventra.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
        if (authentication.isAuthenticated()) {
            String token = jwtUtil.generateToken(authRequest.getEmail());
            return new ResponseEntity<>(new ApiResponse<>(true, "Login Successful", token), HttpStatus.OK);
        } else {
            throw new UsernameNotFoundException("invalid user request !");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> registerUser(@RequestBody RegisterRequest registerRequest) {
        UserResponse registeredUser = userService.registerUser(registerRequest).getData();
        return new ResponseEntity<>(new ApiResponse<>(true, "Registration successful", registeredUser), HttpStatus.CREATED);
    }
}
