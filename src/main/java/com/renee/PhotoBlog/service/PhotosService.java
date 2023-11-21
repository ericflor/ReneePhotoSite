package com.renee.PhotoBlog.service;

import com.renee.PhotoBlog.model.Photo;
import com.renee.PhotoBlog.repo.PhotosRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class PhotosService {

    private final PhotosRepository photosRepository;

    @Autowired
    public PhotosService(PhotosRepository photosRepository) {
        this.photosRepository = photosRepository;
    }

    public Photo savePhoto(Photo photo) {
        return photosRepository.save(photo);
    }

    public List<Photo> getAllPhotos() {
        return photosRepository.findAll();
    }

    public Optional<Photo> getPhotoById(Long id) {
        return photosRepository.findById(id);
    }

    public void deletePhoto(Long id) {
        photosRepository.deleteById(id);
    }
}
