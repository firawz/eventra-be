package com.eventra.service;

import com.eventra.model.User;
import com.eventra.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        Optional<User> userOptional = Optional.empty();

        // Check if the identifier is a valid UUID
        if (isValidUUID(identifier)) {
            try {
                UUID userId = UUID.fromString(identifier);
                userOptional = userRepository.findById(userId);
                if (userOptional.isPresent()) {
                    logger.debug("User found by ID: {}", identifier);
                } else {
                    logger.warn("User not found with ID: {}", identifier);
                }
            } catch (IllegalArgumentException e) {
                logger.debug("Identifier {} is not a valid UUID format, falling back to email search.", identifier);
            }
        }

        // If user not found by ID, try finding by email
        if (userOptional.isEmpty()) {
            userOptional = userRepository.findByEmail(identifier);
            if (userOptional.isPresent()) {
                logger.debug("User found by email: {}", identifier);
            } else {
                logger.warn("User not found with email: {}", identifier);
            }
        }

        User user = userOptional.orElseThrow(() -> new UsernameNotFoundException("User not found with identifier: " + identifier));

        return new org.springframework.security.core.userdetails.User(user.getId().toString(), user.getPassword(), new ArrayList<>());
    }

    private boolean isValidUUID(String uuidString) {
        try {
            UUID.fromString(uuidString);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
