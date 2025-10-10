package com.eventra.dto;

import com.eventra.model.Role;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "Full name is required")
    private String fullName;
    @NotBlank(message = "Email is required")
    private String email;
    private String phone; // Phone can be null
    @NotBlank(message = "Password is required")
    private String password;
    private String role; // Role can be null, defaults to USER in service
    private String gender; // Gender can be null
    private String nik; // NIK can be null
    private Integer wallet; // Wallet can be null
}
