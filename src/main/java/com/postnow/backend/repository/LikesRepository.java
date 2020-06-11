package com.postnow.backend.repository;

import com.postnow.backend.model.PostLike;
import com.postnow.backend.model.Post;
import com.postnow.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikesRepository extends JpaRepository<PostLike, Long> {
    Optional<PostLike> findByPostAndUser(Post post, User user);
}
