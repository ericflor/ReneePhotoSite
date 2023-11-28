package com.renee.PhotoBlog.service;

import com.renee.PhotoBlog.model.User;
import com.renee.PhotoBlog.model.UserRole;
import com.renee.PhotoBlog.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerNewUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(UserRole.USER); // Set the role to USER
        return userRepository.save(user);
    }

    public User checkIfUserAlreadyExists(String username){
        return userRepository.findByUsername(username);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
