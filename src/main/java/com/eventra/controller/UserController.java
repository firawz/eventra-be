package com.eventra.controller;

import com.eventra.dto.ApiResponse;
import com.eventra.dto.EventResponse;
import com.eventra.dto.PaginationResponse;
import com.eventra.dto.UserRequest;
import com.eventra.dto.UserResponse;
import com.eventra.service.UserService;
import java.util.List;
import java.util.Optional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserService userService;

	@GetMapping
	public ResponseEntity<ApiResponse<PaginationResponse<UserResponse>>> getAllUsers(
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int limit,
			@RequestParam(defaultValue = "createdAt") String sortBy,
			@RequestParam(defaultValue = "asc") String sortDir) {
		try {
			PaginationResponse<UserResponse> users = userService.getAllUsers(page, limit, sortBy, sortDir);
			return new ResponseEntity<>(new ApiResponse<>(true, "Users retrieved successfully", users), HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Error retrieving all users: {}", e.getMessage(), e);
			return new ResponseEntity<>(new ApiResponse<>(false, "Error retrieving users: " + e.getMessage(), null),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and #id.toString() == authentication.principal.userId.toString())")
	public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable UUID id) {
		try {
			ApiResponse<UserResponse> serviceResponse = userService.getUserById(id);
			return new ResponseEntity<>(serviceResponse, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Error retrieving user by ID {}: {}", id, e.getMessage(), e);
			return new ResponseEntity<>(new ApiResponse<>(false, "Error retrieving user: " + e.getMessage(), null),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and #id.toString() == authentication.principal.userId.toString())")
	public ResponseEntity<ApiResponse<UserResponse>> updateUser(@PathVariable UUID id,
			@RequestBody UserRequest userRequest) {
		try {
			ApiResponse<UserResponse> serviceResponse = userService.updateUser(id, userRequest);
			return new ResponseEntity<>(serviceResponse, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Error updating user with ID {}: {}", id, e.getMessage(), e);
			return new ResponseEntity<>(new ApiResponse<>(false, "Error updating user: " + e.getMessage(), null),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<UserResponse>> deleteUser(@PathVariable UUID id) {
		try {
			ApiResponse<UserResponse> serviceResponse = userService.deleteUser(id);
			return new ResponseEntity<>(serviceResponse, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Error deleting user with ID {}: {}", id, e.getMessage(), e);
			return new ResponseEntity<>(new ApiResponse<>(false, "Error deleting user: " + e.getMessage(), null),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/{userId}/events")
	@PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and #userId.toString() == authentication.principal.userId.toString())")
	public ResponseEntity<ApiResponse<PaginationResponse<EventResponse>>> getEventsByUserId(
			@PathVariable UUID userId,
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int limit,
			@RequestParam(defaultValue = "createdAt") String sortBy,
			@RequestParam(defaultValue = "asc") String sortDir,
			@RequestParam Optional<String> eventStatus) {
		try {
			ApiResponse<PaginationResponse<EventResponse>> serviceResponse = userService.getEventsByUserId(userId, page, limit, sortBy, sortDir, eventStatus);
			return new ResponseEntity<>(serviceResponse, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Error retrieving events for user ID {}: {}", userId, e.getMessage(), e);
			return new ResponseEntity<>(new ApiResponse<>(false, "Error retrieving events: " + e.getMessage(), null),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
