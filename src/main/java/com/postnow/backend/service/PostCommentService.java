package com.postnow.backend.service;

import com.postnow.backend.repository.PostCommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostCommentService {

    @Autowired
    PostCommentRepository postCommentRepository;
}
