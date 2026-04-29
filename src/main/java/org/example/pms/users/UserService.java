package org.example.pms.users;

import org.example.pms.common.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User getRequired(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
    }

    public UserMeResponse me(UUID id) {
        User user = getRequired(id);
        return new UserMeResponse(user.getId(), user.getEmail(), user.getRole().name(), user.getCreatedAt());
    }

    @Transactional
    public void anonymize(UUID id) {
        User user = getRequired(id);
        user.setDeleted(true);
        user.setEmail("deleted-user-" + user.getId() + "@deleted.local");
        user.setPasswordHash(passwordEncoder.encode(UUID.randomUUID().toString()));
    }
}
