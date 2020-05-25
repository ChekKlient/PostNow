package com.postnow.backend.service;

import com.postnow.backend.model.Post;
import com.postnow.backend.model.PostComment;
import com.postnow.backend.repository.PostCommentRepository;
import com.postnow.backend.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PostCommentService {

    private PostCommentRepository postCommentRepository;
    private PostRepository postRepository;

    @Autowired
    public PostCommentService(PostCommentRepository postCommentRepository, PostRepository postRepository) {
        this.postCommentRepository = postCommentRepository;
        this.postRepository = postRepository;
    }

    public void addCommentToPost(Post post, PostComment postComment){
        Optional<Post> toUpdate = postRepository.findById(post.getId());
        toUpdate.ifPresent(post1 -> post1.getCommentList().add(postComment));
        postRepository.save(toUpdate.get());
    }

    public List<PostComment> findAllCommentsByPostId(Long id){
        Optional<Post> post = postRepository.findById(id);
        List<PostComment> postCommentList = new ArrayList<>();
        post.ifPresent(post1 -> postCommentList.addAll(post1.getCommentList()));
        return postCommentList;
    }
}
