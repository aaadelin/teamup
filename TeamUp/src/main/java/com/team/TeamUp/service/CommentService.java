package com.team.TeamUp.service;

import com.team.TeamUp.domain.Comment;
import com.team.TeamUp.persistence.CommentRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommentService {

    private CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository){
        this.commentRepository = commentRepository;
    }

    public List<Comment> getAll() {
        return commentRepository.findAll();
    }

    public Comment save(Comment newComment) {
        return commentRepository.save(newComment);
    }
}
