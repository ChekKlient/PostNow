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
    @Length(min = 2, max = 500)
    private String text;

    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    private LocalDateTime date;

    private int likes;
    private int comments;
    private int shares;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostComment> commentList = new ArrayList<>();

    public void incrementLikes(){
        this.likes++;
    }
    public void decrementLikes() {
        this.likes--;
    }

    public void incrementShares(){
        this.shares++;
    }

    @PostUpdate
    @PostPersist
    public void postPersist(){
        date = LocalDateTime.now();
    }
}
