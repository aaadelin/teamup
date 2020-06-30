package com.team.teamup.service;

import com.team.teamup.domain.Comment;
import com.team.teamup.persistence.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommentService {

    private final CommentRepository commentRepository;

    @Autowired
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
