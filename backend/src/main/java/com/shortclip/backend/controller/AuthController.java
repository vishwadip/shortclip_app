package com.shortclip.backend.controller;

import com.shortclip.backend.Repository.UserRepository;
import com.shortclip.backend.dto.LoginRequest;
import com.shortclip.backend.dto.LoginResponse;
import com.shortclip.backend.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return userRepository
                .findByUsernameAndPassword(request.getUsername(), request.getPassword())
                .<ResponseEntity<?>>map(user ->
                        ResponseEntity.ok(new LoginResponse(user.getId(),
                                user.getUsername(), user.getRole())))
                .orElseGet(() -> ResponseEntity.status(401).body("Invalid credentials"));
    }
}
