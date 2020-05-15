package com.postnow.backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
public class Post implements Serializable {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false, updatable = false)
    @Length(min = 3, max = 500)
    private String text;

    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    private LocalDate date;

    private int likes;
    private int comments;
    private int shares;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostComment> commentList = new ArrayList<>();

    public void addPost(PostComment comment) {
        commentList.add(comment);
        comment.setPost(this);
    }

    // idn if it'll be available
    public void removePost(PostComment comment) {
        commentList.remove(comment);
        comment.setPost(null);
    }

    @PostUpdate
    @PostPersist
    public void postPersist(){
        date = LocalDate.now();
        likes = 10;
        comments = 10;
        shares = 10;
    }
}
