package com.renee.PhotoBlog.service;

import com.renee.PhotoBlog.exception.ResourceNotFoundException;
import com.renee.PhotoBlog.model.Photo;
import com.renee.PhotoBlog.model.User;
import com.renee.PhotoBlog.repo.PhotosRepository;
import com.renee.PhotoBlog.repo.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class PhotosService {

    private final PhotosRepository photosRepository;
    private final UserRepository userRepository;

    @Autowired
    public PhotosService(PhotosRepository photosRepository, UserRepository userRepository) {
        this.photosRepository = photosRepository;
        this.userRepository = userRepository;
    }

    public Photo savePhotoForUser(Photo photo, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " not found"));
        photo.setUser(user);
        return photosRepository.save(photo);
    }

    public Photo associatePhotoToUser(Long photoId, Long userId) throws ResourceNotFoundException {
        Photo photo = photosRepository.findById(photoId)
                .orElseThrow(() -> new ResourceNotFoundException("Photo with ID " + photoId + " not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " not found"));

        photo.setUser(user);
        return photosRepository.save(photo);
    }

    public List<Photo> getPhotosByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " not found"));
        return photosRepository.findByUser(user);
    }

    public Photo savePhoto(Photo photo) {
        return photosRepository.save(photo);
    }

    public List<Photo> getAllPhotos() {
        return photosRepository.findAll();
    }

    public Optional<Photo> getPhotoById(Long id) {
        return Optional.ofNullable(photosRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Photo with ID " + id + " not found")));
    }

    public void deletePhoto(Long id) {
        if (!photosRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete, photo with ID " + id + " not found");
        }
        photosRepository.deleteById(id);
    }
}
