package com.eventra.dto;

import com.eventra.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
	@Email(message = "Invalid email format")
	private String email;
	@Pattern(regexp = "^(\\+62|0)\\d{9,12}$", message = "Invalid Indonesian phone number format")
	private String phone; // Phone can be null
	@NotBlank(message = "Password is required")
	@Size(min = 6, message = "Password must be at least 6 characters long")
	private String password;
	private String role; // Role can be null, defaults to USER in service
	private String gender; // Gender can be null
	private String nik; // NIK can be null
	private Integer wallet; // Wallet can be null
}
