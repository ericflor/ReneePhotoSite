package com.renee.PhotoBlog.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.renee.PhotoBlog.model.Photo;
import com.renee.PhotoBlog.model.User;
import com.renee.PhotoBlog.model.UserRole;
import com.renee.PhotoBlog.repo.PhotosRepository;
import com.renee.PhotoBlog.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PhotosControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private PhotosRepository photosRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        photosRepository.deleteAll();
        userRepository.deleteAll();
        testUser = createUser();
    }

    private User createUser() {
        User user = new User(null, "testuser", "password", UserRole.USER, null);
        return userRepository.save(user);
    }

    private Photo createTestPhoto() {
        Photo photo = new Photo(null, "Test Title", "2023-01-01", "Test Description", "Test Location", null, testUser);
        return photosRepository.save(photo);
    }

    // Test methods

    @Test
    @WithMockUser(username = "testuser", roles = {"ADMIN"})
    public void testAddPhotoForUser() throws Exception {
        Photo newPhoto = new Photo(null, "Test Title", "2023-01-01", "Test Description", "Test Location", null, testUser);
        mockMvc.perform(post("/photos/user/" + testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPhoto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Title"));
    }

    @Test
    public void testGetPhotosByUserId() throws Exception {
        Photo existingPhoto = createTestPhoto();

        mockMvc.perform(get("/photos/user/" + testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(existingPhoto.getId()));
    }

    @Test
    @WithMockUser(username = "ReneeMichael", roles = {"ADMIN"})
    public void testAddPhotoAsAdmin() throws Exception {
        Photo newPhoto = new Photo(null, "Test Title", "2023-01-01", "Test Description", "Test Location", null, testUser);
        mockMvc.perform(post("/photos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPhoto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Title"));
    }

    @Test
    public void testGetAllPhotos() throws Exception {
        mockMvc.perform(get("/photos"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "ReneeMichael", roles = {"ADMIN"})
    public void testUpdatePhotoAsAdmin() throws Exception {
        Photo existingPhoto = createTestPhoto();
        existingPhoto.setTitle("Updated Title");
        existingPhoto.setDescription("Updated Description");

        mockMvc.perform(put("/photos/" + existingPhoto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(existingPhoto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.description").value("Updated Description"));
    }

    @Test
    @WithMockUser(username = "ReneeMichael", roles = {"ADMIN"})
    public void testDeletePhotoAsAdmin() throws Exception {
        Photo photoToDelete = createTestPhoto();

        mockMvc.perform(delete("/photos/" + photoToDelete.getId()))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetPhotoById() throws Exception {
        Photo existingPhoto = createTestPhoto();

        mockMvc.perform(get("/photos/" + existingPhoto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingPhoto.getId()));
    }
}
