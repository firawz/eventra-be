package com.eventra.dto;

import com.eventra.model.Role;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserResponse {
    private UUID id;
    private String fullName;
    private String email;
    private String phone;
    private Role role;
    private LocalDateTime createdAt;
    private String gender;
    private String nik;
    private Boolean isRegistered;

}
