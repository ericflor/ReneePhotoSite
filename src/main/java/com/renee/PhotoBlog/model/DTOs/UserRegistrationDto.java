package com.renee.PhotoBlog.model.DTOs;

public class UserRegistrationDto {

    private String username;
    private String password;
    // Add other relevant fields

    // Getters and setters
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
}