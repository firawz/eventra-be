package com.eventra.dto;

import com.eventra.model.Role;
import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class UserRequest {
    private String fullName;

    @Email(message = "Invalid email format")
    private String email;

    private String phone;

    private String password;

    private String role; // Changed to String

    private String gender;

    private String nik;
    private Integer wallet;
}
