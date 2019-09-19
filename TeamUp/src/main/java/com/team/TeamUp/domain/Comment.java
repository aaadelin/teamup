package com.team.TeamUp.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String title;

    @Column(length = 1000)
    private String content;

    private LocalDateTime datePosted;

    @OneToOne
    @ToString.Exclude
    private Post post;

    @ManyToOne
    private Comment parent;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User creator;

    @OneToMany
    @JoinColumn(name = "PARENT_COMM")
    private List<Comment> replies = new ArrayList<>();

    public void addReply(Comment reply){
        this.replies.add(reply);
    }
}
