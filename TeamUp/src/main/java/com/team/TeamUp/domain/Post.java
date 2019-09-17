package com.team.TeamUp.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    private Task task;

    @OneToMany
    @JoinColumn(name = "POST_ID")
    private List<Comment> comments = new ArrayList<>();

    public Post() {
    }
    public void addComment(Comment comment){
        this.comments.add(comment);
    }
}
