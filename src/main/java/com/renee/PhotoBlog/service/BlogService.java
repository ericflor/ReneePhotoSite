package com.renee.PhotoBlog.service;

import com.renee.PhotoBlog.exception.ResourceNotFoundException;
import com.renee.PhotoBlog.model.Blog;
import com.renee.PhotoBlog.model.Photo;
import com.renee.PhotoBlog.repo.BlogRepository;
import com.renee.PhotoBlog.repo.PhotosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlogService {

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private PhotosRepository photosRepository;


    public Blog saveBlog(Blog blog) {
        return blogRepository.save(blog);
    }

    public List<Blog> getAllBlogs() {
        return blogRepository.findAll();
    }

    public Blog getBlogById(Long id) throws ResourceNotFoundException {
        return blogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Blog not found with ID: " + id));
    }

    public void deleteBlog(Long id) {
        blogRepository.deleteById(id);
    }

    public Blog addPhotosToBlog(Long blogId, List<Photo> photos) throws ResourceNotFoundException {
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new ResourceNotFoundException("Blog not found with ID: " + blogId));

        blog.getPhotos().addAll(photos);
        return blogRepository.save(blog);
    }

    public Blog associatePhotosToBlog(Long blogId, List<Long> photoIds) throws ResourceNotFoundException {
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new ResourceNotFoundException("Blog not found with ID: " + blogId));

        List<Photo> photos = photosRepository.findAllById(photoIds);
        blog.getPhotos().addAll(photos);
        return blogRepository.save(blog);
    }
}
