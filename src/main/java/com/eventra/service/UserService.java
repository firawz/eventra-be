package com.eventra.service;

import com.eventra.dto.ApiResponse;
import com.eventra.dto.RegisterRequest;
import com.eventra.dto.UserRequest;
import com.eventra.dto.PaginationResponse;
import com.eventra.dto.UserResponse;
import com.eventra.exception.ResourceNotFoundException;
import com.eventra.model.Role;
import com.eventra.model.User;
import com.eventra.repository.EventRepository;
import com.eventra.repository.OrderRepository;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventra.config.CustomUserDetails;
import com.eventra.dto.ApiResponse;
import com.eventra.dto.EventResponse;
import com.eventra.dto.PaginationResponse;
import com.eventra.dto.RegisterRequest;
import com.eventra.dto.UserRequest;
import com.eventra.dto.UserResponse;
import com.eventra.exception.ResourceNotFoundException;
import com.eventra.model.Event;
import com.eventra.model.EventStatus;
import com.eventra.model.Order;
import com.eventra.model.Role;
import com.eventra.model.User;
import com.eventra.repository.EventRepository;
import com.eventra.repository.OrderRepository;
import com.eventra.repository.UserRepository;
import com.eventra.service.AuditService;

@Service
public class UserService {

	private static final Logger logger = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private EventRepository eventRepository;

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

			if (userRequest.getFullName() != null) {
				user.setFullName(userRequest.getFullName());
			}
			if (userRequest.getEmail() != null) {
				user.setEmail(userRequest.getEmail());
			}
			if (userRequest.getPhone() != null) {
				user.setPhone(userRequest.getPhone());
			}
			// Only update password if a new one is provided
			if (userRequest.getPassword() != null && !userRequest.getPassword().isEmpty()) {
				user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
			}
			if (userRequest.getRole() != null) {
				user.setRole(userRequest.getRole());
			}
			if (userRequest.getGender() != null) {
				user.setGender(userRequest.getGender());
			}
			if (userRequest.getNik() != null) {
				user.setNik(userRequest.getNik());
			}

			// Handle wallet update
			if (userRequest.getWallet() != null) {
				Integer currentWallet = user.getWallet() != null ? user.getWallet() : 0;
				user.setWallet(currentWallet + userRequest.getWallet());
			}

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

	public ApiResponse<PaginationResponse<EventResponse>> getEventsByUserId(UUID userId, int page, int limit, String sortBy, String sortDir, Optional<String> eventStatus) {
		try {
			User user = userRepository.findById(userId)
					.orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

			List<Order> orders = orderRepository.findByUser(user);

			Set<UUID> eventIds = orders.stream()
					.map(order -> order.getEvent().getId())
					.collect(Collectors.toSet());

			Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
					: Sort.by(sortBy).descending();
			Pageable pageable = PageRequest.of(page - 1, limit, sort);

			Page<Event> eventPage;
			if (eventStatus.isPresent()) {
				EventStatus status = EventStatus.valueOf(eventStatus.get().toUpperCase());
				eventPage = eventRepository.findByIdInAndStatus(eventIds, status, pageable);
			} else {
				eventPage = eventRepository.findByIdIn(eventIds, pageable);
			}

			List<EventResponse> content = eventPage.getContent().stream()
					.map(this::mapEventToEventResponse)
					.collect(Collectors.toList());

			return new ApiResponse<>(true, "Events retrieved successfully for user", new PaginationResponse<>(
					content,
					eventPage.getNumber() + 1,
					eventPage.getSize(),
					eventPage.getTotalElements(),
					eventPage.getTotalPages(),
					eventPage.isLast()));
		} catch (Exception e) {
			logger.error("Error retrieving events for user ID {}: {}", userId, e.getMessage(), e);
			throw new RuntimeException("Error retrieving events for user: " + e.getMessage());
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
		userResponse.setSub(user.getId()); // Edit : by Gilang change to sub
		userResponse.setFullName(user.getFullName());
		userResponse.setEmail(user.getEmail());
		userResponse.setPhone(user.getPhone()); // Edit by : Gilang Changed to phoneNumber
		userResponse.setRole(user.getRole());
		userResponse.setCreatedAt(user.getCreatedAt());
		userResponse.setGender(user.getGender());
		userResponse.setNik(user.getNik());
		userResponse.setIsRegistered(user.getIsRegistered());
		userResponse.setWallet(user.getWallet());
		return userResponse;
	}

	private EventResponse mapEventToEventResponse(Event event) {
		EventResponse eventResponse = new EventResponse();
		eventResponse.setId(event.getId());
		eventResponse.setTitle(event.getTitle());
		eventResponse.setDescription(event.getDescription());
		eventResponse.setLocation(event.getLocation());
		eventResponse.setStartDate(event.getStartDate());
		eventResponse.setEndDate(event.getEndDate());
		eventResponse.setCreatedAt(event.getCreatedAt());
		eventResponse.setCreatedBy(event.getCreatedBy());
		eventResponse.setUpdatedAt(event.getUpdatedAt());
		eventResponse.setUpdatedBy(event.getUpdatedBy());
		eventResponse.setImageUrl(event.getImageUrl());
		eventResponse.setCategory(event.getCategory());
		eventResponse.setStatus(event.getStatus().name()); // Assuming EventStatus is an enum
		// Tickets are not mapped here as per the current EventResponse DTO structure
		return eventResponse;
	}
}
