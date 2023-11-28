package com.renee.PhotoBlog.model.DTOs;

public class UserRegistrationResponse {

    private String username;
    private String message;
    private Long id;

    public UserRegistrationResponse(Long id, String username, String message) {
        this.id = id;
        this.username = username;
        this.message = message;
    }


    public Long getId(){return id;}

    public void setId(Long id) {this.id = id;}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
