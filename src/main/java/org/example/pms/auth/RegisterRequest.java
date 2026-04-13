package org.example.pms.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.example.pms.users.UserRole;

public record RegisterRequest(
        @Email @NotBlank String email,
        @Size(min = 6) String password,
        UserRole role
) {}
