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
import com.eventra.service.AuditService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.transaction.annotation.Transactional; // Added import
import java.util.Optional; // Import Optional
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.eventra.config.CustomUserDetails;

@Service
public class UserService {

	private static final Logger logger = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
    private AuditService auditService;

	@Transactional // Added Transactional annotation
	public ApiResponse<UserResponse> registerUser(RegisterRequest registerRequest) {
		try {

			if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
				throw new IllegalArgumentException("Email already registered");
			}

			if (registerRequest.getNik() != null && userRepository.findByNik(registerRequest.getNik()).isPresent()) {
				throw new IllegalArgumentException("NIK already registered");
			}

			User newUser = createUserFromRegisterRequest(registerRequest);
			User savedUser = userRepository.save(newUser);
			auditService.publishCreateAudit(savedUser, "CREATE"); // Use new audit method

			return new ApiResponse<>(true, "User created successfully", mapUserToUserResponse(savedUser));
		} catch (Exception e) {
			System.err.println("Error during user registration: " + e.getMessage()); // Changed to System.err
			e.printStackTrace(); // Print full stack trace for detailed debugging
			return new ApiResponse<>(false, e.getMessage(), null);
		}
	}

	public Optional<User> getUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	private User createUserFromRegisterRequest(RegisterRequest registerRequest) {
		User user = new User();
		user.setFullName(registerRequest.getFullName());
		user.setEmail(registerRequest.getEmail());
		user.setPhone(registerRequest.getPhone());
		user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
		user.setRole(registerRequest.getRole() != null ? registerRequest.getRole() : Role.USER.name());
		user.setGender(registerRequest.getGender());
		user.setNik(registerRequest.getNik());
		user.setCreatedAt(LocalDateTime.now());
		user.setCreatedBy(getCurrentAuditor()); // Set createdBy from security context
		user.setIsRegistered(true);
		return user;
	}

	public PaginationResponse<UserResponse> getAllUsers(int page, int limit, String sortBy, String sortDir) {
		try {
			Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
					: Sort.by(sortBy).descending();
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
					userPage.isLast());
		} catch (Exception e) {
			logger.error("Error retrieving all users: {}", e.getMessage(), e);
			throw new RuntimeException("Error retrieving all users: " + e.getMessage());
		}
	}

	public ApiResponse<UserResponse> getUserById(UUID id) {
		try {
			User user = userRepository.findById(id)
					.orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
			return new ApiResponse<>(true, "User retrieved successfully", mapUserToUserResponse(user));
		} catch (Exception e) {
			logger.error("Error retrieving user by ID {}: {}", id, e.getMessage(), e);
			throw new RuntimeException("Error retrieving user by ID: " + e.getMessage());
		}
	}

	public ApiResponse<UserResponse> updateUser(UUID id, UserRequest userRequest) {
		try {
			User user = userRepository.findById(id)
					.orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

			user.setFullName(userRequest.getFullName());
			user.setEmail(userRequest.getEmail());
			user.setPhone(userRequest.getPhone());
			// Only update password if a new one is provided
			if (userRequest.getPassword() != null && !userRequest.getPassword().isEmpty()) {
				user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
			}
			user.setRole(userRequest.getRole() != null ? userRequest.getRole() : user.getRole()); // Keep existing role if
																									// not provided
			user.setGender(userRequest.getGender());
			user.setNik(userRequest.getNik());
			user.setUpdatedAt(LocalDateTime.now()); // Set updatedAt
			user.setUpdatedBy(getCurrentAuditor()); // Set updatedBy from security context

			User updatedUser = userRepository.save(user);
			auditService.publishUpdateAudit(updatedUser, "UPDATE"); // Use new audit method
			return new ApiResponse<>(true, "User updated successfully", mapUserToUserResponse(updatedUser));
		} catch (Exception e) {
			logger.error("Error updating user with ID {}: {}", id, e.getMessage(), e);
			throw new RuntimeException("Error updating user: " + e.getMessage());
		}
	}

	public ApiResponse<UserResponse> deleteUser(UUID id) {
		try {
			User userToDelete = userRepository.findById(id)
					.orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));
			userRepository.deleteById(id);
			auditService.publishDeleteAudit(userToDelete, "DELETE"); // Use new audit method
			return new ApiResponse<>(true, "User deleted successfully", null);
		} catch (Exception e) {
			logger.error("Error deleting user with ID {}: {}", id, e.getMessage(), e);
			throw new RuntimeException("Error deleting user: " + e.getMessage());
		}
	}

	private String getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomUserDetails) {
                CustomUserDetails customUserDetails = (CustomUserDetails) principal;
                return customUserDetails.getUserId().toString();
            } else {
                logger.warn("Principal is not CustomUserDetails. Returning 'SYSTEM' for auditor.");
                return "SYSTEM";
            }
        }
        return "SYSTEM";
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
