package com.postnow.backend.service;

import com.postnow.backend.model.Post;
import com.postnow.backend.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    @Autowired
    PostRepository postRepository;

    public List<Post> findAllByOrderByDateDesc() {
        return postRepository.findAllByOrderByDateDesc();
    }

    public void createPost(Post post){
        post.setCommentList(new LinkedList<>());
        post.setComments(0);
        post.setLikes(0);
        post.setShares(0);

        postRepository.save(post);
    }

    public void incrementLikes(Post post){
        Optional<Post> optionalPost = postRepository.findById(post.getId());
        optionalPost.ifPresent(Post::incrementLikes);
    }

    public void decrementLikes(Post post){
        Optional<Post> optionalPost = postRepository.findById(post.getId());
        optionalPost.ifPresent(Post::decrementLikes);
    }
}
