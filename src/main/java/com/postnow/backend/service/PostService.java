package com.postnow.backend.service;

import com.postnow.backend.model.Post;
import com.postnow.backend.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

    @Autowired
    PostRepository postRepository;

    public List<Post> findAll() {
        return postRepository.findAll();
    }

}
