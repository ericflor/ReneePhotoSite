package com.renee.PhotoBlog.repo;

import com.renee.PhotoBlog.model.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotosRepository extends JpaRepository<Photo, Long> {
}
