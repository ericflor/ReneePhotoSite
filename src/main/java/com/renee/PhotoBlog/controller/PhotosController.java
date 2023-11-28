package com.renee.PhotoBlog.controller;

import com.renee.PhotoBlog.exception.ResourceNotFoundException;
import com.renee.PhotoBlog.model.Photo;
import com.renee.PhotoBlog.service.PhotosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/photos")
public class PhotosController {

    @Autowired
    private final PhotosService photosService;

    public PhotosController(PhotosService photosService) {
        this.photosService = photosService;
    }



    @PostMapping("/bulk")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Photo>> addMultiplePhotos(@RequestBody List<Photo> photos) {
        List<Photo> savedPhotos = photosService.saveMultiplePhotos(photos);
        return ResponseEntity.ok(savedPhotos);
    }

    @PostMapping("/bulk/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addMultiplePhotosForUser(@RequestBody List<Photo> photos, @PathVariable Long userId) {
        try {
            List<Photo> savedPhotos = photosService.saveMultiplePhotosForUser(photos, userId);
            return ResponseEntity.ok(savedPhotos);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }
    }

    @PutMapping("/bulk/associate/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> associateMultiplePhotosToUser(@RequestBody List<Long> photoIds, @PathVariable Long userId) {
        try {
            List<Photo> updatedPhotos = photosService.associateMultiplePhotosToUser(photoIds, userId);
            return ResponseEntity.ok(updatedPhotos);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Photo addPhotoForUser(@RequestBody Photo photo, @PathVariable Long userId) {
        return photosService.savePhotoForUser(photo, userId);
    }

    @PutMapping("/associate/{photoId}/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> associatePhotoToUser(@PathVariable Long photoId, @PathVariable Long userId) {
        try {
            Photo updatedPhoto = photosService.associatePhotoToUser(photoId, userId);
            return ResponseEntity.ok(updatedPhoto);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Photo addPhoto(@RequestBody Photo photo) {
        return photosService.savePhoto(photo);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Photo updatePhoto(@PathVariable Long id, @RequestBody Photo photo) {
        if (!photo.getId().equals(id)) {
            throw new IllegalArgumentException("Photo ID doesn't match URL ID");
        }
        return photosService.savePhoto(photo);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deletePhoto(@PathVariable Long id) {
        photosService.deletePhoto(id);
    }

    @GetMapping("/user/{userId}")
    public List<Photo> getPhotosByUserId(@PathVariable Long userId) {
        return photosService.getPhotosByUserId(userId);
    }

    @GetMapping
    public List<Photo> getAllPhotos() {
        return photosService.getAllPhotos();
    }

    @GetMapping("/{id}")
    public Optional<Photo> getPhotoById(@PathVariable Long id) {
        return photosService.getPhotoById(id);
    }

}
