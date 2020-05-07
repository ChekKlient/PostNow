package com.postnow.backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
public class User implements Serializable {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Email
    @Length(min = 6, max = 35)
    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = false, nullable = false)
    private String password;

    @Column(nullable = false)
    private Boolean active;

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<UserRole> roles;

    @OneToOne(cascade=CascadeType.ALL)
    private UserAdditionalData userAdditionalData = new UserAdditionalData();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> postList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostComment> commentList = new ArrayList<>();

    public void addPost(Post post) {
        postList.add(post);
        post.setUser(this);
    }

    public void removePost(Post post) {
        postList.remove(post);
        post.setUser(null);
    }
}
