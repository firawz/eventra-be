package com.eventra.controller;

import com.eventra.dto.ApiResponse;
import com.eventra.dto.UserRequest;
import com.eventra.dto.UserResponse;
import com.eventra.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> registerUser(@RequestBody UserRequest userRequest) {
        ApiResponse<UserResponse> registeredUserResponse = userService.registerUser(userRequest);
        return new ResponseEntity<>(registeredUserResponse, HttpStatus.CREATED);
    }

    // You would typically add a login endpoint here, potentially returning a JWT
    // For simplicity, this example only includes registration.
}
