package com.renee.PhotoBlog.controller;

import com.renee.PhotoBlog.model.DTOs.JwtAuthenticationResponse;
import com.renee.PhotoBlog.model.DTOs.LoginRequest;
import com.renee.PhotoBlog.model.DTOs.UserRegistrationDto;
import com.renee.PhotoBlog.model.DTOs.UserRegistrationResponse;
import com.renee.PhotoBlog.model.UserRole;
import com.renee.PhotoBlog.security.JwtTokenProvider;
import com.renee.PhotoBlog.model.User;
import com.renee.PhotoBlog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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
        // Check if the username already exists
        User existingUser = userService.checkIfUserAlreadyExists(registrationDto.getUsername());
        if (existingUser != null) {
            // Username already exists
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Error: Username is already taken!");
        }

        // Proceed with registration
        User newUser = new User();
        newUser.setUsername(registrationDto.getUsername());
        newUser.setPassword(registrationDto.getPassword());
        newUser.setRole(UserRole.USER); // Assign USER role

        User registeredUser = userService.registerNewUser(newUser);
        return ResponseEntity.ok(new UserRegistrationResponse(registeredUser.getId(), registeredUser.getUsername(), "User successfully registered"));
    }


    // Endpoint to get all users (Admin only)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // Endpoint to find a user's ID by username (Admin only)
    @GetMapping("/find/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUserIdByUsername(@PathVariable String username) {
        User user = userService.checkIfUserAlreadyExists(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        return ResponseEntity.ok("The user's ID is: " + user.getId());
    }
}
