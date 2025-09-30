package com.eventra.controller;

import com.eventra.dto.ApiResponse;
import com.eventra.dto.PaginationResponse;
import com.eventra.dto.UserRequest;
import com.eventra.dto.UserResponse;
import com.eventra.service.UserService;
import java.util.List;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<PaginationResponse<UserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        PaginationResponse<UserResponse> users = userService.getAllUsers(page, limit, sortBy, sortDir);
        return new ResponseEntity<>(new ApiResponse<>(true, "Users retrieved successfully", users), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable UUID id) {
        ApiResponse<UserResponse> serviceResponse = userService.getUserById(id);
        return new ResponseEntity<>(serviceResponse, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(@PathVariable UUID id, @Valid @RequestBody UserRequest userRequest) {
        ApiResponse<UserResponse> serviceResponse = userService.updateUser(id, userRequest);
        return new ResponseEntity<>(serviceResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> deleteUser(@PathVariable UUID id) {
        ApiResponse<UserResponse> serviceResponse = userService.deleteUser(id);
        return new ResponseEntity<>(serviceResponse, HttpStatus.OK);
    }
}
