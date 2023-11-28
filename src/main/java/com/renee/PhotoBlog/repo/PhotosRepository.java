package com.renee.PhotoBlog.repo;

import com.renee.PhotoBlog.model.Photo;
import com.renee.PhotoBlog.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotosRepository extends JpaRepository<Photo, Long> {

    List<Photo> findByUser(User user);
}
