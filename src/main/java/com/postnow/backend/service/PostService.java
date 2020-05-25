package com.postnow.backend.service;

import com.postnow.backend.model.Post;
import com.postnow.backend.model.PostComment;
import com.postnow.backend.model.User;
import com.postnow.backend.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.ArrayList;
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
        post.setShares(0);

        postRepository.save(post);
    }

    public void iLikeIt(Post post, User whoLike){
        Optional<Post> optionalPost = postRepository.findById(post.getId());
        optionalPost.ifPresent(post1 -> post1.iLikeIt(whoLike));
        postRepository.save(optionalPost.get());
    }

    public void iDontLikeIt(Post post, User whoDislike){
        Optional<Post> optionalPost = postRepository.findById(post.getId());
        optionalPost.ifPresent(post1 -> post1.iDontLikeIt(whoDislike));
        postRepository.save(optionalPost.get());
    }

    public void incrementShares(Post post){
        Optional<Post> optionalPost = postRepository.findById(post.getId());
        optionalPost.ifPresent(Post::incrementShares);
        postRepository.save(optionalPost.get());
    }
}
