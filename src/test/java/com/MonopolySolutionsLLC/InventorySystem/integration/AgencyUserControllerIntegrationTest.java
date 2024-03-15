package com.MonopolySolutionsLLC.InventorySystem.integration;

import com.MonopolySolutionsLLC.InventorySystem.model.Agency;
import com.MonopolySolutionsLLC.InventorySystem.model.Enums.AgencyLevel;
import com.MonopolySolutionsLLC.InventorySystem.model.DTOs.LoginRequest;
import com.MonopolySolutionsLLC.InventorySystem.model.Enums.UserRole;
import com.MonopolySolutionsLLC.InventorySystem.repo.AgencyRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AgencyUserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private AgencyRepository agencyRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;



    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        agencyRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testAddAgencyAsEmployee() throws Exception {
        // Mimic JSON structure as it would be received from the UI, including the level as a string
        String newAgencyJson = """
        {
          "name": "Eric",
          "email": "eric@email.com",
          "username": "ericflorence",
          "password": "password",
          "level": "EMPLOYEE"
        }
    """;

        mockMvc.perform(post("/agencies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newAgencyJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("EMPLOYEE")); // Assert the role is automatically set to EMPLOYEE
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testAddAgencyAsRetailer() throws Exception {
        // Mimic JSON structure as it would be received from the UI, including the level as a string
        String newAgencyJson = """
        {
          "name": "Retail Agency",
          "email": "retailagency@email.com",
          "username": "retailagency",
          "password": "password",
          "level": "RETAILER"
        }
    """;

        mockMvc.perform(post("/agencies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newAgencyJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Retail Agency"))
                .andExpect(jsonPath("$.email").value("retailagency@email.com"))
                .andExpect(jsonPath("$.username").value("retailagency"))
                .andExpect(jsonPath("$.level").value("RETAILER"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testGetAgencies() throws Exception {
        mockMvc.perform(get("/agencies"))
                .andExpect(status().isOk());
    }

    @Test
    public void testUserAuthentication() throws Exception {
        Agency newAgent = new Agency();
        newAgent.setName("ADMIN");
        newAgent.setEmail("admin@email.com");
        newAgent.setUsername("admin");
        newAgent.setPassword(passwordEncoder.encode("sl@123"));
        newAgent.setRole(UserRole.ADMIN);

        agencyRepository.save(newAgent);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("sl@123");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testAddAgentWithExistingUsername() throws Exception {

        Agency existingAgent = new Agency();
        existingAgent.setName("First Agent");
        existingAgent.setEmail("firstagent@email.com");
        existingAgent.setUsername("uniqueUsername");
        existingAgent.setPassword(passwordEncoder.encode("securePassword"));
        existingAgent.setLevel(AgencyLevel.EMPLOYEE);
        agencyRepository.save(existingAgent);

        Agency newAgent = new Agency();
        newAgent.setName("Second Agent");
        newAgent.setEmail("secondagent@email.com");
        newAgent.setUsername("uniqueUsername"); // Re-using the username
        newAgent.setPassword("anotherPassword");
        newAgent.setLevel(AgencyLevel.DISTRIBUTOR);
        // Attempt to save the new agent with the duplicate username
        mockMvc.perform(post("/agencies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newAgent)))
                .andExpect(status().is5xxServerError())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof RuntimeException))
                .andExpect(result -> assertEquals("The username: uniqueUsername already exists.", result.getResolvedException().getMessage()));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testFindUserById() throws Exception {
        Agency newUser = new Agency(2L, "ADMIN","admin@email.com",
                "authUser", "password",
                AgencyLevel.RETAILER, UserRole.ADMIN);
        Agency savedUser = agencyRepository.save(newUser);

        mockMvc.perform(get("/agencies/" + savedUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedUser.getId()))
                .andExpect(jsonPath("$.name").value("ADMIN"))
                .andExpect(jsonPath("$.username").value("authUser"))
                .andExpect(jsonPath("$.level").value("RETAILER"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testUpdateAgency() throws Exception {

        Agency originalAgency = new Agency();
        originalAgency.setName("Original Name");
        originalAgency.setEmail("original@email.com");
        originalAgency.setUsername("originalUsername");
        originalAgency.setPassword(passwordEncoder.encode("originalPassword"));
        originalAgency.setLevel(AgencyLevel.DISTRIBUTOR);
        Agency savedAgency = agencyRepository.save(originalAgency);

        Agency updatedDetails = new Agency();
        updatedDetails.setName("Updated Name");
        updatedDetails.setEmail("updated@email.com");
        updatedDetails.setUsername("updatedUsername");
        updatedDetails.setPassword("updatedPassword"); // The password should be sent plain here and encoded in the service
        updatedDetails.setLevel(AgencyLevel.MASTER_AGENT);

        mockMvc.perform(put("/agencies/" + savedAgency.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.email").value("updated@email.com"))
                .andExpect(jsonPath("$.username").value("updatedUsername"))
                .andExpect(jsonPath("$.level").value("MASTER_AGENT"));

        Optional<Agency> updatedAgencyOpt = agencyRepository.findById(savedAgency.getId());
        assertTrue(updatedAgencyOpt.isPresent());
        Agency updatedAgency = updatedAgencyOpt.get();
        assertEquals("Updated Name", updatedAgency.getName());
        assertEquals("updated@email.com", updatedAgency.getEmail());
        assertEquals("updatedUsername", updatedAgency.getUsername());
    }
}
