package com.eventra.controller;

import com.eventra.config.JwtUtil;
import com.eventra.dto.ApiResponse;
import com.eventra.dto.AuthRequest;
import com.eventra.dto.RegisterRequest;
import com.eventra.dto.UserResponse;
import com.eventra.service.UserService;
import jakarta.validation.Valid; // Added for validation
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
            if (authentication.isAuthenticated()) {
                String token = jwtUtil.generateToken(authRequest.getEmail());
                return new ResponseEntity<>(new ApiResponse<>(true, "Login Successful", token), HttpStatus.OK);
            } else {
                throw new UsernameNotFoundException("invalid user request !");
            }
        } catch (UsernameNotFoundException e) {
            logger.warn("Authentication failed for user {}: {}", authRequest.getEmail(), e.getMessage());
            return new ResponseEntity<>(new ApiResponse<>(false, "Invalid credentials", null), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            logger.error("Error during login for user {}: {}", authRequest.getEmail(), e.getMessage(), e);
            return new ResponseEntity<>(new ApiResponse<>(false, "An unexpected error occurred during login: " + e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            ApiResponse<UserResponse> response = userService.registerUser(registerRequest);
            if (response.isSuccess()) {
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (IllegalArgumentException e) {
            logger.warn("Registration failed for user {}: {}", registerRequest.getEmail(), e.getMessage());
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage(), null), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Error during user registration for user {}: {}", registerRequest.getEmail(), e.getMessage(), e);
            return new ResponseEntity<>(new ApiResponse<>(false, "An unexpected error occurred during registration: " + e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
