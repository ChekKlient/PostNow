package com.postnow.backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
    @Length(min = 2, max = 500)
    private String text;

    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    private LocalDateTime date;
    private int shares;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "post_id")
    private List<PostComment> commentList =  new ArrayList<>(); // linkedlist

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(name = "post_likes", joinColumns = @JoinColumn(name = "post_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> likesList = new HashSet<>();

    public void iLikeIt(User whoLike){
        this.likesList.add(whoLike);
    }

    public void iDontLikeIt(User whoDislike) {
        this.likesList.remove(whoDislike);
        likesList.removeIf(s -> s.getId().equals(whoDislike.getId()));
    }

    public void incrementShares(){
        this.shares++;
    }

    @PostPersist
    public void postPersist(){
        date = LocalDateTime.now();
    }
}
