package com.eventra.dto;

import com.eventra.model.Role;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserResponse {
    private UUID sub; // Edit : by Gilang change to sub
    private String fullName;
    private String email;
    private String phone; // Edit by : Gilang Changed to phoneNumber
    private String role; // Changed to String
    private LocalDateTime createdAt;
    private String gender;
    private String nik;
    private Boolean isRegistered;
    private Integer wallet;
	private UUID id;

}
