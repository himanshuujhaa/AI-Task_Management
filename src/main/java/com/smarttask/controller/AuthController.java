package com.smarttask.controller;

import com.smarttask.dto.AuthResponse;
import com.smarttask.dto.LoginRequest;
import com.smarttask.dto.SignUpRequest;
import com.smarttask.model.entity.User;
import com.smarttask.repository.UserRepository;
import com.smarttask.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.smarttask.constants.Constants.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/signup")
    public String signup(@RequestBody SignUpRequest request) {

        if(userRepository.existsByEmail(request.getEmail())) {
            return EMAIL_ALREADY_EXISTS;
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);

        return USER_REGISTERED_SUCCESSFULLY;
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException(USER_NOT_FOUND));

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException(INVALID_CREDENTIALS);
        }

        String token = jwtUtil.generateToken(user.getEmail());

        return new AuthResponse(token);
    }
}
