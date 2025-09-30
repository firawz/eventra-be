package com.eventra.service;

import com.eventra.dto.ApiResponse;
import com.eventra.dto.RegisterRequest;
import com.eventra.dto.UserRequest;
import com.eventra.dto.PaginationResponse;
import com.eventra.dto.UserResponse;
import com.eventra.exception.ResourceNotFoundException;
import com.eventra.model.Role;
import com.eventra.model.User;
import com.eventra.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ApiResponse<UserResponse> registerUser(RegisterRequest registerRequest) {
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = new User();
        user.setFullName(registerRequest.getFullName());
        user.setEmail(registerRequest.getEmail());
        user.setPhone(registerRequest.getPhone());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(registerRequest.getRole() != null ? registerRequest.getRole() : Role.USER); // Default to USER if not provided
        user.setGender(registerRequest.getGender());
        user.setNik(registerRequest.getNik());
        user.setCreatedAt(LocalDateTime.now());
        user.setIsRegistered(true); // Assuming registration means it's registered

        User savedUser = userRepository.save(user);
        return new ApiResponse<>(true, "User created successfully", mapUserToUserResponse(savedUser));
    }

    public PaginationResponse<UserResponse> getAllUsers(int page, int limit, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page - 1, limit, sort);
        Page<User> userPage = userRepository.findAll(pageable);

        List<UserResponse> content = userPage.getContent().stream()
                .map(this::mapUserToUserResponse)
                .collect(Collectors.toList());

        return new PaginationResponse<>(
                content,
                userPage.getNumber() + 1,
                userPage.getSize(),
                userPage.getTotalElements(),
                userPage.getTotalPages(),
                userPage.isLast()
        );
    }

    public ApiResponse<UserResponse> getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        return new ApiResponse<>(true, "User retrieved successfully", mapUserToUserResponse(user));
    }

    public ApiResponse<UserResponse> updateUser(UUID id, UserRequest userRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        user.setFullName(userRequest.getFullName());
        user.setEmail(userRequest.getEmail());
        user.setPhone(userRequest.getPhone());
        // Only update password if a new one is provided
        if (userRequest.getPassword() != null && !userRequest.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        }
        user.setRole(userRequest.getRole() != null ? userRequest.getRole() : user.getRole()); // Keep existing role if not provided
        user.setGender(userRequest.getGender());
        user.setNik(userRequest.getNik());

        User updatedUser = userRepository.save(user);
        return new ApiResponse<>(true, "User updated successfully", mapUserToUserResponse(updatedUser));
    }

    public ApiResponse<UserResponse> deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found with ID: " + id);
        }
        userRepository.deleteById(id);
        return new ApiResponse<>(true, "User deleted successfully", null);
    }

    private UserResponse mapUserToUserResponse(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setFullName(user.getFullName());
        userResponse.setEmail(user.getEmail());
        userResponse.setPhone(user.getPhone());
        userResponse.setRole(user.getRole());
        userResponse.setCreatedAt(user.getCreatedAt());
        userResponse.setGender(user.getGender());
        userResponse.setNik(user.getNik());
        userResponse.setIsRegistered(user.getIsRegistered());
        return userResponse;
    }
}
