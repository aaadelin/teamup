package com.team.TeamUp.controller;

import com.team.TeamUp.persistance.*;
import com.team.TeamUp.utils.DTOsConverter;
import com.team.TeamUp.validation.UserValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public abstract class AbstractRestController {
    protected TeamRepository teamRepository;
    protected UserRepository userRepository;
    protected TaskRepository taskRepository;
    protected ProjectRepository projectRepository;
    protected CommentRepository commentRepository;
    protected PostRepository postRepository;

    protected final DTOsConverter dtOsConverter;
    protected UserValidation userValidation;

    @Autowired
    public AbstractRestController(TeamRepository teamRepository, UserRepository userRepository,
                                  TaskRepository taskRepository, ProjectRepository projectRepository,
                                  CommentRepository commentRepository, PostRepository postRepository,
                                  UserValidation userValidation, DTOsConverter dtOsConverter) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.userValidation = userValidation;
        this.dtOsConverter = dtOsConverter;
    }

}
