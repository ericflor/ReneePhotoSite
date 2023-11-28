package com.renee.PhotoBlog.model.DTOs;

import com.renee.PhotoBlog.model.UserRole;

public class UserRegistrationDto {

    private Long id;
    private String username;
    private String password;
    private UserRole role;


    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() { return role; }

    public void setRole(UserRole role) { this.role = role; }
}