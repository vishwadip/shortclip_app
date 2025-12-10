// src/main/java/com/shortclip/backend/dto/LoginResponse.java
package com.shortclip.backend.dto;

public class LoginResponse {
    private Long id;
    private String username;
    private String role;

    public LoginResponse(Long id, String username, String role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
}
