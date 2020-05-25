package com.postnow.backend.repository;

import com.postnow.backend.model.Post;
import com.postnow.backend.model.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByOrderByDateDesc();
}
