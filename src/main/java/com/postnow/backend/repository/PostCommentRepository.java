package com.postnow.backend.repository;

import com.postnow.backend.model.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {
}
