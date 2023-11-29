package com.renee.PhotoBlog.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.renee.PhotoBlog.model.Blog;
import com.renee.PhotoBlog.model.Photo;
import com.renee.PhotoBlog.repo.BlogRepository;
import com.renee.PhotoBlog.repo.PhotosRepository;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class BlogControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private PhotosRepository photosRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        blogRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testCreateBlog() throws Exception {
        Blog blog = new Blog();
        blog.setTitle("Test Blog");
        blog.setBody("This is a test blog.");
        blog.setPhotos(Collections.emptyList());

        mockMvc.perform(post("/blogs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(blog)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Blog"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testGetAllBlogs() throws Exception {
        mockMvc.perform(get("/blogs"))
                .andExpect(status().isOk());
    }

    // Test updating a blog
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testUpdateBlog() throws Exception {
        Blog originalBlog = new Blog();
        originalBlog.setTitle("Original Title");
        originalBlog.setBody("Original body.");
        originalBlog.setPhotos(Collections.emptyList());
        Blog savedBlog = blogRepository.save(originalBlog);

        Blog updatedBlog = new Blog();
        updatedBlog.setTitle("Updated Title");
        updatedBlog.setBody("Updated body.");
        updatedBlog.setPhotos(Collections.emptyList());

        mockMvc.perform(put("/blogs/" + savedBlog.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedBlog)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.body").value("Updated body."));
    }

    // Test deleting a blog
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testDeleteBlog() throws Exception {
        Blog blogToDelete = new Blog();
        blogToDelete.setTitle("Title for Deletion");
        blogToDelete.setBody("Body for Deletion");
        Blog savedBlog = blogRepository.save(blogToDelete);

        mockMvc.perform(delete("/blogs/" + savedBlog.getId()))
                .andExpect(status().isOk());
    }

    // Test getting a blog by ID
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testGetBlogById() throws Exception {
        Blog blog = new Blog();
        blog.setTitle("Specific Title");
        blog.setBody("Specific body.");
        Blog savedBlog = blogRepository.save(blog);

        mockMvc.perform(get("/blogs/" + savedBlog.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Specific Title"))
                .andExpect(jsonPath("$.body").value("Specific body."));
    }

    // Test adding photos to a blog
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testAddPhotosToBlog() throws Exception {
        // Create and save the blog
        Blog blog = new Blog();
        blog.setTitle("Blog for Photos");
        blog.setBody("Blog body.");
        Blog savedBlog = blogRepository.save(blog);

        // Create and save photos
        Photo photo1 = new Photo(null, "Photo Title 1", null, null, null, null, null);
        Photo photo2 = new Photo(null, "Photo Title 2", null, null, null, null, null);
        Photo savedPhoto1 = photosRepository.save(photo1);
        Photo savedPhoto2 = photosRepository.save(photo2);

        // List of photos to add to blog
        List<Photo> photosToAdd = Arrays.asList(savedPhoto1, savedPhoto2);

        mockMvc.perform(post("/blogs/" + savedBlog.getId() + "/photos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(photosToAdd)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.photos[0].title").value("Photo Title 1"))
                .andExpect(jsonPath("$.photos[1].title").value("Photo Title 2"));
    }

    // Test associating existing photos to a blog
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testAssociatePhotosToBlog() throws Exception {
        Blog blog = new Blog();
        blog.setTitle("Blog for Existing Photos");
        blog.setBody("Blog body.");
        Blog savedBlog = blogRepository.save(blog);

        Photo photo1 = new Photo(null, "Existing Photo 1", null, null, null, null, null);
        Photo photo2 = new Photo(null, "Existing Photo 2", null, null, null, null, null);
        Photo savedPhoto1 = photosRepository.save(photo1);
        Photo savedPhoto2 = photosRepository.save(photo2);

        List<Long> photoIds = Arrays.asList(savedPhoto1.getId(), savedPhoto2.getId());

        mockMvc.perform(put("/blogs/" + savedBlog.getId() + "/associate-photos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(photoIds)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.photos[0].id").value(savedPhoto1.getId()))
                .andExpect(jsonPath("$.photos[1].id").value(savedPhoto2.getId()));
    }
}
