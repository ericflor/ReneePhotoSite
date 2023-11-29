package com.renee.PhotoBlog.controller;

import com.renee.PhotoBlog.exception.ResourceNotFoundException;
import com.renee.PhotoBlog.model.Blog;
import com.renee.PhotoBlog.model.Photo;
import com.renee.PhotoBlog.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/blogs")
public class BlogController {

    @Autowired
    private BlogService blogService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Blog> createBlog(@RequestBody Blog blog) {
        return ResponseEntity.ok(blogService.saveBlog(blog));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Blog>> getAllBlogs() {
        return ResponseEntity.ok(blogService.getAllBlogs());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Blog> getBlogById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(blogService.getBlogById(id));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Blog> updateBlog(@PathVariable Long id, @RequestBody Blog blogDetails) {
        try {
            Blog blog = blogService.getBlogById(id);
            blog.setTitle(blogDetails.getTitle());
            blog.setBody(blogDetails.getBody());
            blog.setPhotos(blogDetails.getPhotos());
            return ResponseEntity.ok(blogService.saveBlog(blog));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteBlog(@PathVariable Long id) {
        try {
            blogService.deleteBlog(id);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{blogId}/photos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Blog> addPhotosToBlog(@PathVariable Long blogId, @RequestBody List<Photo> photos) {
        try {
            Blog updatedBlog = blogService.addPhotosToBlog(blogId, photos);
            return ResponseEntity.ok(updatedBlog);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{blogId}/associate-photos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Blog> associatePhotosToBlog(@PathVariable Long blogId, @RequestBody List<Long> photoIds) {
        try {
            Blog updatedBlog = blogService.associatePhotosToBlog(blogId, photoIds);
            return ResponseEntity.ok(updatedBlog);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
