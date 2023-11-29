package com.renee.PhotoBlog.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.renee.PhotoBlog.model.DTOs.LoginRequest;
import com.renee.PhotoBlog.model.DTOs.UserRegistrationDto;
import com.renee.PhotoBlog.model.User;
import com.renee.PhotoBlog.model.UserRole;
import com.renee.PhotoBlog.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;



    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        userRepository.deleteAll();
    }

    @Test
    public void testUserRegistration() throws Exception {
        UserRegistrationDto registrationDto = new UserRegistrationDto();
        registrationDto.setUsername("newUser");
        registrationDto.setPassword("password");
        registrationDto.setRole(UserRole.USER);

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("newUser"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testGetAllUsers() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());
    }

    @Test
    public void testUserAuthentication() throws Exception {
        UserRegistrationDto registrationDto = new UserRegistrationDto();
        registrationDto.setUsername("authUser");
        registrationDto.setPassword("password");
        registrationDto.setRole(UserRole.USER);
        userRepository.save(new User(null, registrationDto.getUsername(), passwordEncoder.encode(registrationDto.getPassword()), UserRole.USER, null));

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("authUser");
        loginRequest.setPassword("password");

        mockMvc.perform(post("/users/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testFindUserById() throws Exception {
        User newUser = new User(null, "findUser", passwordEncoder.encode("password"), UserRole.USER, null);
        User savedUser = userRepository.save(newUser);

        mockMvc.perform(get("/users/find/" + savedUser.getUsername()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("The user's ID is: " + savedUser.getId()));
    }
}
