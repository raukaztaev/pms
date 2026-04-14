package org.example.pms.auth;

import org.example.pms.common.exception.ApiException;
import org.example.pms.common.security.JwtService;
import org.example.pms.users.User;
import org.example.pms.users.UserRepository;
import org.example.pms.users.UserRole;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ApiException(HttpStatus.CONFLICT, "Email already exists");
        }
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(request.email().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(request.role() == null ? UserRole.USER : request.role());
        user.setCreatedAt(Instant.now());
        user.setDeleted(false);
        userRepository.save(user);
        return tokenFor(user);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        User user = userRepository.findByEmail(request.email()).orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
        if (user.isDeleted()) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        return tokenFor(user);
    }

    private AuthResponse tokenFor(User user) {
        String token = jwtService.generate(user.getId(), user.getEmail(), user.getRole().name());
        return new AuthResponse(token, new AuthResponse.UserDto(user.getId(), user.getEmail(), user.getRole().name()));
    }
}
