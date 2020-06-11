package com.postnow.backend.service;

import com.postnow.backend.model.PostLike;
import com.postnow.backend.model.Post;
import com.postnow.backend.model.User;
import com.postnow.backend.repository.LikesRepository;
import com.postnow.backend.repository.PostRepository;
import com.postnow.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.openmbean.KeyAlreadyExistsException;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private PostRepository postRepository;
    private LikesRepository likesRepository;
    private UserRepository userRepository;

    @Autowired
    public PostService(PostRepository postRepository, LikesRepository likesRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.likesRepository = likesRepository;
        this.userRepository = userRepository;
    }

    public List<Post> findAllByOrderByDateDesc() {
        return postRepository.findAllByOrderByDateDesc();
    }

    public void createPost(Post post) {
        post.setShares(0);
        postRepository.save(post);
    }

    public void iLikeIt(Post post, User whoLike) {
        Optional<Post> optionalPost = postRepository.findById(post.getId());
        Optional<User> optionalUser = userRepository.findById(whoLike.getId());

        optionalPost.ifPresent(post1 -> {

            optionalUser.ifPresent(user1 -> {
                PostLike postLike = new PostLike();
                postLike.setPost(post1);
                postLike.setUser(user1);

                if (likesRepository.findByPostAndUser(post1, user1).isPresent())
                    throw new KeyAlreadyExistsException("Already exist");

                likesRepository.save(postLike);
            });
        });
    }

    public void iDontLikeIt(Post post, User whoDislike) {
        likesRepository.findByPostAndUser(post, whoDislike).
                ifPresent(like1 -> likesRepository.deleteById(like1.getId()));
    }

    public void incrementShares(Post post) {
        Optional<Post> optionalPost = postRepository.findById(post.getId());
        optionalPost.ifPresent(value -> {
            value.incrementShares();
            postRepository.save(value);
        });
    }

    public boolean didILikeIt(Post post, User user) {
        return likesRepository.findByPostAndUser(post, user).isPresent();
    }
}
