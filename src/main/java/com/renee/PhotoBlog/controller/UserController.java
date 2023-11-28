package com.renee.PhotoBlog.controller;

import com.renee.PhotoBlog.model.DTOs.JwtAuthenticationResponse;
import com.renee.PhotoBlog.model.DTOs.LoginRequest;
import com.renee.PhotoBlog.model.DTOs.UserRegistrationDto;
import com.renee.PhotoBlog.model.DTOs.UserRegistrationResponse;
import com.renee.PhotoBlog.security.JwtTokenProvider;
import com.renee.PhotoBlog.model.User;
import com.renee.PhotoBlog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
@Validated
public class UserController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserService userService;

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationDto registrationDto) {
        User user = new User();
        user.setUsername(registrationDto.getUsername());
        user.setPassword(registrationDto.getPassword());

        User registeredUser = userService.registerNewUser(user);
        return ResponseEntity.ok(new UserRegistrationResponse(registeredUser.getUsername(), "User successfully registered"));
    }
}
